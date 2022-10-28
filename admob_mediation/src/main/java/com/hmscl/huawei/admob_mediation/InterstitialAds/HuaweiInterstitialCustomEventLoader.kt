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


package com.hmscl.huawei.admob_mediation.InterstitialAds

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.mediation.MediationAdLoadCallback
import com.google.android.gms.ads.mediation.MediationInterstitialAd
import com.google.android.gms.ads.mediation.MediationInterstitialAdCallback
import com.google.android.gms.ads.mediation.MediationInterstitialAdConfiguration
import com.hmscl.huawei.admob_mediation.CustomEventError
import com.hmscl.huawei.admob_mediation.configureAdRequest
import com.huawei.hms.ads.AdListener
import com.huawei.hms.ads.InterstitialAd

/** Interstitial custom event loader for Huawei Ads SDK.  */
class HuaweiInterstitialCustomEventLoader(
    /** Configuration for requesting the interstitial ad from the third party networks.  */
    private val mediationInterstitialAdConfiguration: MediationInterstitialAdConfiguration,
    /** Callback that fires on loading success or failure.  */
    private val mediationAdLoadCallback: MediationAdLoadCallback<MediationInterstitialAd, MediationInterstitialAdCallback>
) : MediationInterstitialAd {
    private val TAG = HuaweiInterstitialCustomEventLoader::class.java.simpleName
    private var context: Context? = null

    /** Huawei interstitial ad.  */
    private lateinit var huaweiInterstitialView: InterstitialAd

    /** Callback for interstitial ad events.  */
    private var interstitialAdCallback: MediationInterstitialAdCallback? = null

    /** Loads the interstitial ad from the third party ad network.  */
    fun loadAd() {
        Log.d(TAG, "InterstitialEventLoader - loadAd()")
        if(context == null){
            mediationAdLoadCallback.onFailure(
                CustomEventError.createCustomEventNoActivityContextError()
            )
            return
        }
        context = mediationInterstitialAdConfiguration.context

        // All custom events have a server parameter named "parameter" that returns back the parameter
        // entered into the AdMob UI when defining the custom event.
        val serverParameter =
            mediationInterstitialAdConfiguration.serverParameters.getString("parameter")
        if (serverParameter.isNullOrBlank()) {
            mediationAdLoadCallback.onFailure(
                CustomEventError.createCustomEventNoAdIdError()
            )
            return
        }
        huaweiInterstitialView = InterstitialAd(context)
        huaweiInterstitialView.adId = serverParameter

        // Implement a HuaweiAdListener and forward callbacks to mediation.
        val adListener = object : AdListener() {
            override fun onAdLoaded() {
                Log.d(
                    TAG,
                    "InterstitialEventLoader - loadAd() - onAdLoaded() - Ad loaded successfully"
                )
                interstitialAdCallback =
                    mediationAdLoadCallback.onSuccess(this@HuaweiInterstitialCustomEventLoader)
            }

            override fun onAdFailed(errorCode: Int) {
                Log.e(
                    TAG,
                    "InterstitialEventLoader - loadAd() - onAdFailed() - Failed to load Huawei Interstitial with code: ${errorCode}."
                )
                val adError = AdError(
                    errorCode, "AdFailed",
                    CustomEventError.SAMPLE_SDK_DOMAIN
                )
                mediationAdLoadCallback.onFailure(adError)
            }

            override fun onAdOpened() {
                Log.d(TAG, "InterstitialEventLoader - loadAd() - onAdOpened()")
                interstitialAdCallback?.onAdOpened()
            }

            override fun onAdClicked() {
                Log.d(TAG, "InterstitialEventLoader - loadAd() - onAdClicked()")
                interstitialAdCallback?.reportAdClicked()
            }

            override fun onAdLeave() {
                Log.d(TAG, "InterstitialEventLoader - loadAd() - onAdLeave()")
                interstitialAdCallback?.onAdLeftApplication()
            }

            override fun onAdClosed() {
                Log.d(TAG, "InterstitialEventLoader - loadAd() - onAdClosed()")
                interstitialAdCallback?.onAdClosed()
            }
        }
        huaweiInterstitialView.adListener = adListener

        // Make an ad request.
        huaweiInterstitialView.loadAd(mediationInterstitialAdConfiguration.configureAdRequest())
    }

    override fun showAd(p0: Context) {
        Log.d(TAG, "InterstitialEventLoader - showAd()")
        if (huaweiInterstitialView.isLoaded) {
            huaweiInterstitialView.show(p0 as Activity?)
        }
    }
}