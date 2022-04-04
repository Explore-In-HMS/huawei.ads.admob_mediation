/*
 *   Copyright 2022. Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.hmscl.huawei.admob_mediation.RewardedAds

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.google.ads.consent.ConsentInformation
import com.google.ads.consent.ConsentStatus
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.mediation.MediationAdLoadCallback
import com.google.android.gms.ads.mediation.MediationRewardedAd
import com.google.android.gms.ads.mediation.MediationRewardedAdCallback
import com.google.android.gms.ads.mediation.MediationRewardedAdConfiguration
import com.hmscl.huawei.admob_mediation.CustomEventError
import com.hmscl.huawei.admob_mediation.ErrorCode
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.HwAds
import com.huawei.hms.ads.NonPersonalizedAd
import com.huawei.hms.ads.reward.*

/** Rewarded custom event loader for the Huawei Ads SDK.  */
class HuaweiRewardedCustomEventLoader(
    /** Configuration for requesting the rewarded ad from the third party network.  */
    private val mediationRewardedAdConfiguration: MediationRewardedAdConfiguration,
    /**
     * A [MediationAdLoadCallback] that handles any callback when a Sample rewarded ad finishes
     * loading.
     */
    private val mediationRewardedAdLoadCallback: MediationAdLoadCallback<MediationRewardedAd, MediationRewardedAdCallback>
) : MediationRewardedAd {
    private var context: Context? = null
    private val TAG by lazy { HuaweiRewardedCustomEventLoader::class.java.simpleName }

    /**
     * Represents a [RewardAd].
     */
    private lateinit var sampleRewardedAd: RewardAd

    /**
     * Used to forward rewarded video ad events to the Google Mobile Ads SDK.
     */
    private var rewardedAdCallback: MediationRewardedAdCallback? = null

    /** Loads the rewarded ad from Huawei Ads network.  */
    fun loadAd() {
        Log.d(TAG, "RewardedEventLoader - loadAd()")
        this.context = mediationRewardedAdConfiguration.context
        // All custom events have a server parameter named "parameter" that returns back the parameter
        // entered into the AdMob UI when defining the custom event.
        val serverParameter =
            mediationRewardedAdConfiguration.serverParameters.getString("parameter")
        if (TextUtils.isEmpty(serverParameter)) {
            mediationRewardedAdLoadCallback.onFailure(
                AdError(
                    CustomEventError.ERROR_NO_AD_UNIT_ID,
                    "Ad unit id is empty",
                    CustomEventError.CUSTOM_EVENT_ERROR_DOMAIN
                )
            )
            return
        }
        sampleRewardedAd = RewardAd(context, serverParameter)

        val rewardAdListener : RewardAdListener = object : RewardAdListener {
            override fun onRewarded(p0: Reward?) {
                if (p0 != null) {
                    Log.d(
                        TAG,
                        "RewardedEventLoader - loadAd() - onRewarded() = ${p0.name} + ${p0.amount}"
                    )
                    rewardedAdCallback?.onUserEarnedReward(
                        HuaweiCustomEventRewardedItemMapper(
                            p0.name,
                            p0.amount
                        )
                    )
                }
            }

            override fun onRewardAdClosed() {
                Log.d(TAG, "RewardedEventLoader - loadAd() - onRewardAdClosed()")
                rewardedAdCallback!!.onAdClosed()
            }

            override fun onRewardAdFailedToLoad(p0: Int) {
                Log.e(
                    TAG,
                    "RewardedEventLoader - loadAd() - onRewardAdFailedToLoad() - Failed to load Huawei rewarded with code: ${p0}."
                )
                mediationRewardedAdLoadCallback.onFailure(
                    CustomEventError.createSampleSdkError(
                        ErrorCode.UNKNOWN
                    )
                )
            }

            override fun onRewardAdLeftApp() {
                Log.d(TAG, "RewardedEventLoader - loadAd() - onRewardAdLeftApp()")

            }

            override fun onRewardAdLoaded() {
                Log.d(TAG, "RewardedEventLoader - loadAd() - onRewardAdLoaded()")
                rewardedAdCallback = mediationRewardedAdLoadCallback.onSuccess(this@HuaweiRewardedCustomEventLoader)
            }

            override fun onRewardAdOpened() {
                Log.d(TAG, "RewardedEventLoader - loadAd() - onRewardAdOpened()")
                rewardedAdCallback!!.reportAdClicked()
            }

            override fun onRewardAdCompleted() {
                Log.d(TAG, "RewardedEventLoader - loadAd() - onRewardAdCompleted()")
                rewardedAdCallback!!.onVideoComplete()
            }

            override fun onRewardAdStarted() {
                Log.d(TAG, "RewardedEventLoader - loadAd() - onRewardAdStarted()")
                rewardedAdCallback!!.onAdOpened()
            }

        }
        sampleRewardedAd.rewardAdListener = rewardAdListener

        val adParam = AdParam.Builder()

        val bundle = mediationRewardedAdConfiguration.mediationExtras
        var content = "{"
        bundle.keySet()?.forEach { key ->
            adParam.addKeyword(key)
            Log.d("MediationKeywordsLog", key.toString())
            content += "\""+key+"\""+ ":[\"" +bundle.get(key) + "\"],"
        }
        content.dropLast(1)
        content += "}"

        adParam.setContentBundle(content)

        /**
         * NPA-PA
         */
        try {
            val consentStatus: ConsentStatus =
                ConsentInformation.getInstance(mediationRewardedAdConfiguration.context).consentStatus
            if (consentStatus == ConsentStatus.NON_PERSONALIZED)
                adParam.setNonPersonalizedAd(NonPersonalizedAd.ALLOW_NON_PERSONALIZED)
            else if (consentStatus == ConsentStatus.PERSONALIZED)
                adParam.setNonPersonalizedAd(NonPersonalizedAd.ALLOW_ALL)
        } catch (exception: java.lang.Exception) {
            Log.e(this.toString(), "configureAdRequest: Consent status couldn't read")
        }

        /**
         * TCF2.0
         */
        try {
            val sharedPref = mediationRewardedAdConfiguration.context?.getSharedPreferences(
                "SharedPreferences",
                Context.MODE_PRIVATE
            )
            val tcfString = sharedPref?.getString("IABTCF_TCString", "");

            if (tcfString != null && tcfString != "") {
                val requestOptions = HwAds.getRequestOptions()
                requestOptions.toBuilder().setConsent(tcfString).build()
            }
        } catch (exception: java.lang.Exception) {
            Log.e(this.toString(), "configureAdRequest: TCFString couldn't read")
        }

        val listenerRewarded = object : RewardAdLoadListener() {
            override fun onRewardAdFailedToLoad(p0: Int) {
                super.onRewardAdFailedToLoad(p0)
                Log.e(
                    TAG,
                    "RewardedEventLoader - loadAd() - onRewardAdFailedToLoad() = $p0"
                )
                mediationRewardedAdLoadCallback.onFailure(AdError(p0, "Rewarded Ads", "onFailure"))
            }

            override fun onRewardedLoaded() {
                super.onRewardedLoaded()
                Log.d(TAG, "RewardedEventLoader - loadAd() - onRewardedLoaded() - Ad loaded successfully")
                rewardedAdCallback =
                    mediationRewardedAdLoadCallback.onSuccess(this@HuaweiRewardedCustomEventLoader)
            }
        }
        sampleRewardedAd.loadAd(adParam.build(),listenerRewarded)
    }

    override fun showAd(context: Context) {
        Log.d(TAG, "RewardedEventLoader - showAd()")
        if (context !is Activity) {
            rewardedAdCallback?.onAdFailedToShow(
                CustomEventError.createCustomEventNoActivityContextError()
            )
            return
        }
        if (!sampleRewardedAd!!.isLoaded) {
            rewardedAdCallback?.onAdFailedToShow(
                CustomEventError.createCustomEventAdNotAvailableError()
            )
            return
        }
        sampleRewardedAd!!.show(context)
    }
}