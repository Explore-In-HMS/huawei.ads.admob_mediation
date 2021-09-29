/*
 *   Copyright 2021. Explore in HMS. All rights reserved.
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
import android.util.Log
import com.google.ads.consent.ConsentInformation
import com.google.ads.consent.ConsentStatus
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.mediation.MediationAdLoadCallback
import com.google.android.gms.ads.mediation.MediationRewardedAd
import com.google.android.gms.ads.mediation.MediationRewardedAdCallback
import com.google.android.gms.ads.mediation.MediationRewardedAdConfiguration
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.HwAds
import com.huawei.hms.ads.NonPersonalizedAd
import com.huawei.hms.ads.reward.Reward
import com.huawei.hms.ads.reward.RewardAd
import com.huawei.hms.ads.reward.RewardAdLoadListener
import com.huawei.hms.ads.reward.RewardAdStatusListener

class HuaweiCustomEventRewardedAdEventForwarder(
        private val adConfiguration: MediationRewardedAdConfiguration,
        private val mediationAdLoadCallBack: MediationAdLoadCallback<MediationRewardedAd, MediationRewardedAdCallback>
): HuaweiCustomEventRewardedAdListener(),MediationRewardedAd {
    private lateinit var rewardedAdCallback: MediationRewardedAdCallback
    private lateinit var rewardAd: RewardAd
    private var rewardAdId = "testx9dtjwj8hp"
    fun load(adUnit: String?) {
        if (adUnit != null) {
            rewardAdId = adUnit
        }
        this.rewardedAdCallback = mediationAdLoadCallBack.onSuccess(this)
    }

    override fun showAd(context: Context?) {
        rewardAd = RewardAd(context, rewardAdId)
        val listener = object : RewardAdLoadListener() {
            override fun onRewardAdFailedToLoad(p0: Int) {
                super.onRewardAdFailedToLoad(p0)
            }

            override fun onRewardedLoaded() {
                rewardAd.show(context as Activity?,object : RewardAdStatusListener() {
                    override fun onRewardAdClosed() {
                        rewardedAdCallback.onAdClosed()
                    }

                    override fun onRewardAdFailedToShow(errorCode: Int) {
                        rewardedAdCallback.onAdFailedToShow(AdError(errorCode,"Rewarded Ads","Failed to show"))
                    }

                    override fun onRewardAdOpened() {
                        rewardedAdCallback.onAdOpened()
                    }

                    override fun onRewarded(reward: Reward) {
                        rewardedAdCallback.onUserEarnedReward(HuaweiCustomEventRewardedItemMapper(reward.name,reward.amount))
                    }
                })
            }
        }

        val adParam = AdParam.Builder()

        /**
         * NPA-PA
         */
        try {
            val consentStatus: ConsentStatus =
                ConsentInformation.getInstance(context).consentStatus
            if (consentStatus == ConsentStatus.NON_PERSONALIZED)
                adParam.setNonPersonalizedAd(NonPersonalizedAd.ALLOW_NON_PERSONALIZED)
            else if (consentStatus == ConsentStatus.PERSONALIZED)
                adParam.setNonPersonalizedAd(NonPersonalizedAd.ALLOW_ALL)
        } catch (exception: java.lang.Exception) {
            Log.i(this.toString(), "configureAdRequest: Consent status couldn't read")
        }

        /**
         * TCF2.0
         */
        try {
            val sharedPref = context?.getSharedPreferences(
                "SharedPreferences",
                Context.MODE_PRIVATE
            )
            val tcfString = sharedPref?.getString("IABTCF_TCString", "");

            if (tcfString != null && tcfString != "") {
                val requestOptions = HwAds.getRequestOptions()
                requestOptions.toBuilder().setConsent(tcfString).build()
            }
        } catch (exception: java.lang.Exception) {
            Log.i(this.toString(), "configureAdRequest: TCFString couldn't read")
        }

        rewardAd.loadAd(adParam.build(), listener)
    }
}