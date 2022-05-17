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

package com.hmscl.huawei.admob_mediation.BannerAds

import android.content.Context
import android.content.res.Resources
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.mediation.MediationAdLoadCallback
import com.google.android.gms.ads.mediation.MediationBannerAd
import com.google.android.gms.ads.mediation.MediationBannerAdCallback
import com.google.android.gms.ads.mediation.MediationBannerAdConfiguration
import com.hmscl.huawei.admob_mediation.CustomEventError
import com.hmscl.huawei.admob_mediation.configureAdRequest
import com.huawei.hms.ads.AdListener
import com.huawei.hms.ads.BannerAdSize
import com.huawei.hms.ads.banner.BannerView

/** Banner custom event loader for Huawei Ads SDK.  */
class HuaweiBannerCustomEventLoader(
    /** Configuration for requesting the banner ad from the third party networks.  */
    private val mediationBannerAdConfiguration: MediationBannerAdConfiguration,
    /** Callback that fires on loading success or failure.  */
    private val mediationAdLoadCallback: MediationAdLoadCallback<MediationBannerAd, MediationBannerAdCallback>
) : MediationBannerAd {
    private val TAG = HuaweiBannerCustomEventLoader::class.java.simpleName

    /** View to contain the sample banner ad.  */
    private lateinit var huaweiBannerView: BannerView
    private var context: Context? = null

    /** Callback for banner ad events.  */
    private var bannerAdCallback: MediationBannerAdCallback? = null

    /** Loads a banner ad from Huawei Ads network.  */
    fun loadAd() {
        Log.d(TAG, "BannerEventLoader - loadAd()")
        // All custom events have a server parameter named "parameter" that returns back the parameter
        // entered into the AdMob UI when defining the custom event.
        this.context = mediationBannerAdConfiguration.context
        val serverParameter: String =
            mediationBannerAdConfiguration.serverParameters.getString("parameter").toString()
        if (TextUtils.isEmpty(serverParameter)) {
            mediationAdLoadCallback.onFailure(
                AdError(
                    CustomEventError.ERROR_NO_AD_UNIT_ID,
                    "Ad unit id is empty",
                    CustomEventError.CUSTOM_EVENT_ERROR_DOMAIN
                )
            )
            return
        }
        val context = mediationBannerAdConfiguration.context
        huaweiBannerView = BannerView(context)

        // Assumes that the serverParameter is the AdUnitId for Huawei Ads network.
        huaweiBannerView.adId = serverParameter
        val size = mediationBannerAdConfiguration.adSize
        //huaweiBannerView.bannerAdSize = BannerAdSize(size.width, size.height)

        // Internally, smart banners use constants to represent their ad size, which means a call to
        // AdSize.getHeight could return a negative value. Below code used for adjusting to match
        // the device's display metrics.
        val widthInPixels = size.getWidthInPixels(context)
        val heightInPixels = size.getHeightInPixels(context)
        val displayMetrics = Resources.getSystem().displayMetrics
        val widthInDp = Math.round(widthInPixels / displayMetrics.density)
        val heightInDp = Math.round(heightInPixels / displayMetrics.density)
        huaweiBannerView.bannerAdSize = BannerAdSize(widthInDp, heightInDp)
        val adListener: AdListener = object : AdListener() {
            override fun onAdLoaded() {
                Log.d(TAG, "BannerEventLoader - loadAd() - onAdLoaded() - Ad loaded successfully")
                bannerAdCallback =
                    mediationAdLoadCallback.onSuccess(this@HuaweiBannerCustomEventLoader)

            }

            override fun onAdFailed(errorCode: Int) {
                Log.e(
                    TAG,
                    "BannerEventLoader - loadAd() - onAdFailed() - Failed to load Huawei banner with code: ${errorCode}."
                )
                val adError = AdError(
                    errorCode, "AdFailed",
                    CustomEventError.SAMPLE_SDK_DOMAIN
                )
                mediationAdLoadCallback.onFailure(adError)
            }

            override fun onAdOpened() {
                Log.d(TAG, "BannerEventLoader - loadAd() - onAdOpened()")
                bannerAdCallback?.onAdOpened()
            }

            override fun onAdClicked() {
                Log.d(TAG, "BannerEventLoader - loadAd() - onAdClicked()")
                bannerAdCallback?.reportAdClicked()
            }

            override fun onAdLeave() {
                Log.d(TAG, "BannerEventLoader - loadAd() - onAdLeave()")
                bannerAdCallback?.onAdLeftApplication()
            }

            override fun onAdClosed() {
                Log.d(TAG, "BannerEventLoader - loadAd() - onAdClosed()")
                bannerAdCallback?.onAdClosed()
            }
        }
        huaweiBannerView.adListener = adListener
        huaweiBannerView.loadAd(mediationBannerAdConfiguration.configureAdRequest())

    }

    override fun getView(): View {
        return huaweiBannerView
    }
}