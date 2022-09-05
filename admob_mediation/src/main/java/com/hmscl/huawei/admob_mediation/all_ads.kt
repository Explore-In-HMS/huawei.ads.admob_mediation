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

package com.hmscl.huawei.admob_mediation

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.WindowManager
import com.google.ads.consent.ConsentInformation
import com.google.ads.consent.ConsentStatus
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.mediation.*
import com.google.android.gms.ads.mediation.customevent.*
import com.google.android.gms.ads.reward.mediation.MediationRewardedVideoAdAdapter
import com.hmscl.huawei.admob_mediation.BannerAds.HuaweiCustomEventBannerEventForwarder
import com.hmscl.huawei.admob_mediation.InterstitialAds.HuaweiCustomEventInterstitialEventForwarder
import com.hmscl.huawei.admob_mediation.NativeAds.HuaweiCustomEventNativeAdsEventForwarder
import com.hmscl.huawei.admob_mediation.NativeAds.HuaweiCustomEventNativeAdsLoadedEventForwarder
import com.hmscl.huawei.admob_mediation.RewardedAds.HuaweiCustomEventRewardedAdEventForwarder
import com.huawei.hms.ads.*
import com.huawei.hms.ads.banner.BannerView
import com.huawei.hms.ads.nativead.NativeAdConfiguration
import com.huawei.hms.ads.nativead.NativeAdLoader
import java.io.PrintWriter
import java.io.StringWriter


class all_ads : Adapter(),
    CustomEventBanner, CustomEventInterstitial, CustomEventNative {
    private val TAG = all_ads::class.java.simpleName

    private lateinit var huaweiBannerView: BannerView
    private var huaweiBannerAdId = "testw6vs28auh3"

    private lateinit var huaweiInterstitialView: InterstitialAd
    private var huaweiInterstitialAdId = "testb4znbuh3n2"

    private lateinit var nativeAdLoader: NativeAdLoader
    private var huaweiNativeAdId = "testu7m3hc4gvm"

    private var huaweiRewardedAdId = "testx9dtjwj8hp"

    private var context: Context? = null

    private var mInitializationCompleteCallback: InitializationCompleteCallback? = null


    override fun requestBannerAd(
        context: Context?,
        listener: CustomEventBannerListener,
        serverParameters: String?,
        size: AdSize,
        mediationAdRequest: MediationAdRequest,
        mediationExtras: Bundle?
    ) {
        try {
            Log.d(TAG, "enter requestBannerAd")
            if (serverParameters.isNullOrEmpty()) {
                Log.d(TAG, "Banner serverParameter is empty or null")
            }
            this.context = context
            huaweiBannerView = BannerView(context)
            val eventForwarder = HuaweiCustomEventBannerEventForwarder(listener, huaweiBannerView)
            huaweiBannerView.adListener = eventForwarder
            if (serverParameters != null) {
                huaweiBannerAdId = serverParameters
                Log.d(TAG, "Banner serverParameter $serverParameters")
            }

            huaweiBannerView.adId = huaweiBannerAdId
            huaweiBannerView.bannerAdSize =
                context?.let { getHuaweiBannerAdSizeFromAdmobAdSize(adSize = size) }
            huaweiBannerView.loadAd(configureAdRequest(mediationAdRequest))

        } catch (e: Throwable) {
            val stacktrace =
                StringWriter().also { e.printStackTrace(PrintWriter(it)) }.toString().trim()
            Log.e(TAG, "Request Banner Ad Failed: $stacktrace")
            huaweiBannerView.adListener.onAdFailed(AdParam.ErrorCode.INNER)
        }
    }

    private fun getHuaweiBannerAdSizeFromAdmobAdSize(adSize: AdSize): BannerAdSize {

        var resultAdSize: BannerAdSize

        if (AdSize.SMART_BANNER.equals(adSize) || adSize.isFullWidth && adSize.isAutoHeight) {
            resultAdSize = BannerAdSize.BANNER_SIZE_SMART
        } else if (AdSize.BANNER.equals(adSize)) {
            resultAdSize = BannerAdSize.BANNER_SIZE_320_50
        } else if (AdSize.LARGE_BANNER.equals(adSize)) {
            resultAdSize = BannerAdSize.BANNER_SIZE_320_100
        } else if (AdSize.MEDIUM_RECTANGLE.equals(adSize)) {
            resultAdSize = BannerAdSize.BANNER_SIZE_300_250
        } else if (AdSize.FULL_BANNER.equals(adSize)) {
            resultAdSize = BannerAdSize.BANNER_SIZE_468_60
        } else if (AdSize.LEADERBOARD.equals(adSize)) {
            resultAdSize = BannerAdSize.BANNER_SIZE_728_90
        } else if (AdSize.WIDE_SKYSCRAPER.equals(adSize)) {
            resultAdSize = BannerAdSize.BANNER_SIZE_160_600
        } else if (isFullWidthRequest(adSize)) {
            resultAdSize = BannerAdSize.BANNER_SIZE_ADVANCED
        } else {
            resultAdSize = BannerAdSize.getCurrentDirectionBannerSize(context, adSize.width)
            if (resultAdSize == BannerAdSize.BANNER_SIZE_INVALID) {
                resultAdSize = BannerAdSize(adSize.width, adSize.height)
            }
        }

        return resultAdSize
    }

    private fun isFullWidthRequest(adSize: AdSize): Boolean {

        val defaultDisplay: Display =
            (context!!.getSystemService("window") as WindowManager).defaultDisplay
        val displayMetrics = DisplayMetrics()
        defaultDisplay.getMetrics(displayMetrics)

        val screenWidth = (displayMetrics.widthPixels / displayMetrics.density).toInt()
        return adSize.width == screenWidth
    }

    override fun requestInterstitialAd(
        context: Context?,
        listener: CustomEventInterstitialListener,
        serverParameters: String?,
        mediationAdRequest: MediationAdRequest,
        mediationExtras: Bundle?
    ) {
        try {
            Log.d(TAG, "enter requestInterstitialAd")
            if (serverParameters.isNullOrEmpty()) {
                Log.d(TAG, "Interstitial serverParameter is empty or null")
            }
            this.context = context
            huaweiInterstitialView = InterstitialAd(context)
            huaweiInterstitialView.adListener = HuaweiCustomEventInterstitialEventForwarder(
                listener
            )
            if (serverParameters != null) {
                huaweiInterstitialAdId = serverParameters
                Log.d(TAG, "Interstitial serverParameter $serverParameters")
            }
            huaweiInterstitialView.adId = huaweiInterstitialAdId
            huaweiInterstitialView.loadAd(configureAdRequest(mediationAdRequest))
        } catch (e: Throwable) {
            val stacktrace =
                StringWriter().also { e.printStackTrace(PrintWriter(it)) }.toString().trim()
            Log.e(TAG, "Request Interstitial Ad Failed: $stacktrace")
            huaweiInterstitialView.adListener.onAdFailed(AdParam.ErrorCode.INNER)
        }

    }

    override fun showInterstitial() {
        if (huaweiInterstitialView.isLoaded) {
            huaweiInterstitialView.show()
        }
    }

    override fun requestNativeAd(
        context: Context,
        listener: CustomEventNativeListener?,
        serverParameter: String?,
        mediationAdRequest: NativeMediationAdRequest,
        customEventExtras: Bundle?
    ) {

        try {
            Log.d(TAG, "Enter requestNativeAd")
            if (serverParameter.isNullOrEmpty()) {
                Log.d(TAG, "Native serverParameter is empty or null")
            }
            this.context = context
            val options = mediationAdRequest.nativeAdOptions

            if (!mediationAdRequest.isUnifiedNativeAdRequested) {
                listener?.onAdFailedToLoad(AdRequest.ERROR_CODE_INVALID_REQUEST)
                return
            }
            Log.d(
                TAG, "MediationAdRequest = ${
                    mediationAdRequest.isUnifiedNativeAdRequested
                }"
            )
            val videoConfiguration: VideoConfiguration
            val adConfiguration: NativeAdConfiguration

            if (options != null && options.videoOptions != null) {

                videoConfiguration = VideoConfiguration.Builder()
                    .setStartMuted(options.videoOptions!!.startMuted)
                    .setClickToFullScreenRequested(options.videoOptions!!.clickToExpandRequested)
                    .setCustomizeOperateRequested(options.videoOptions!!.customControlsRequested)
                    .build()
                adConfiguration = NativeAdConfiguration.Builder()
                    .setVideoConfiguration(videoConfiguration)
                    .setMediaAspect(options.mediaAspectRatio)
                    .setChoicesPosition(NativeAdConfiguration.ChoicesPosition.INVISIBLE)
                    .build()
            } else {

                videoConfiguration = VideoConfiguration.Builder()
                    .setStartMuted(true)
                    .build()
                adConfiguration = NativeAdConfiguration.Builder()
                    .setVideoConfiguration(videoConfiguration)
                    .setChoicesPosition(NativeAdConfiguration.ChoicesPosition.INVISIBLE)
                    .build()
            }

            Log.d(
                TAG,
                "adConfiguration" + options.videoOptions?.customControlsRequested.toString() + options.videoOptions?.startMuted.toString()
            )

            if (serverParameter != null) {
                huaweiNativeAdId = serverParameter
                Log.d(TAG, "Native serverParameter $serverParameter")
            }
            val loadedEventForwarder = HuaweiCustomEventNativeAdsLoadedEventForwarder(
                listener!!,
                options,
                context
            )
            val adEventForwarder = HuaweiCustomEventNativeAdsEventForwarder(listener, options)
            val builder = NativeAdLoader.Builder(context, huaweiNativeAdId)
            builder.setNativeAdOptions(adConfiguration)
            builder.setNativeAdLoadedListener { nativeAd ->
                if (!nativeAdLoader.isLoading) {
                    loadedEventForwarder.onNativeAdLoaded(nativeAd)
                }
            }.setAdListener(adEventForwarder)

            nativeAdLoader = builder.build()
            nativeAdLoader.loadAd(configureAdRequest(mediationAdRequest))
        } catch (e: Throwable) {
            val stacktrace =
                StringWriter().also { e.printStackTrace(PrintWriter(it)) }.toString().trim()
            Log.e(TAG, "Request Native Ad Failed: $stacktrace")
        }

    }


    private fun configureAdRequest(bannerAdRequest: MediationAdRequest): AdParam {
        val adParam = AdParam.Builder()
        bannerAdRequest.keywords?.forEach { keyword ->
            adParam.addKeyword(keyword)
            Log.d(TAG, "MediationKeywordsLog:" + keyword.toString())
        }

        /**
         * NPA-PA
         */
        try {
            val consentStatus: ConsentStatus =
                ConsentInformation.getInstance(this.context).consentStatus
            if (consentStatus == ConsentStatus.NON_PERSONALIZED)
                adParam.setNonPersonalizedAd(NonPersonalizedAd.ALLOW_NON_PERSONALIZED)
            else if (consentStatus == ConsentStatus.PERSONALIZED)
                adParam.setNonPersonalizedAd(NonPersonalizedAd.ALLOW_ALL)
        } catch (exception: Throwable) {
            val stacktrace =
                StringWriter().also { exception.printStackTrace(PrintWriter(it)) }.toString().trim()
            Log.w(TAG, "configureAdRequest: Consent status couldn't read: $stacktrace")
        }

        /**
         * TCF2.0
         */
        try {
            val sharedPref = context?.getSharedPreferences(
                "SharedPreferences",
                Context.MODE_PRIVATE
            )
            val tcfString = sharedPref?.getString("IABTCF_TCString", "")

            if (tcfString != null && tcfString != "") {
                val requestOptions = HwAds.getRequestOptions()
                requestOptions.toBuilder().setConsent(tcfString).build()
            }
        } catch (exception: Throwable) {
            val stacktrace =
                StringWriter().also { exception.printStackTrace(PrintWriter(it)) }.toString().trim()
            Log.e(TAG, "configureAdRequest: TCFString couldn't read: $stacktrace")
        }

        /**
         * COPPA
         */
        try {
            adParam.setTagForChildProtection(bannerAdRequest.taggedForChildDirectedTreatment())
            Log.d(TAG,"TagforChildLog:" + bannerAdRequest.taggedForChildDirectedTreatment().toString())
        }
        catch (exception:Throwable){
            val stacktrace =
                StringWriter().also { exception.printStackTrace(PrintWriter(it)) }.toString().trim()
            Log.e(TAG, "configureAdRequest: TagForChildProtection couldn't read: $stacktrace")

        }

        return adParam.build()
    }

    override fun onDestroy() {
        if (huaweiBannerView != null) {
            huaweiBannerView.destroy()
        }
    }

    override fun onPause() {
        if (huaweiBannerView != null) {
            huaweiBannerView.pause()
        }
    }

    override fun onResume() {
        if (huaweiBannerView != null) {
            huaweiBannerView.resume()
        }
    }

    override fun initialize(
        context: Context?,
        initializationCompleteCallback: InitializationCompleteCallback,
        mediationConfiguration: MutableList<MediationConfiguration>?
    ) {
        Log.d(TAG, "enter initialize")

        mInitializationCompleteCallback = initializationCompleteCallback

        mInitializationCompleteCallback?.onInitializationSucceeded()

    }


    override fun getVersionInfo(): VersionInfo {
        //TODO Update version info for each release
        return VersionInfo(1, 2, 13)
    }

    override fun getSDKVersionInfo(): VersionInfo {
        //TODO Update version info for each release
        return VersionInfo(3, 4, 54)
    }

    override fun loadRewardedAd(
        mediationRewardedAdConfiguration: MediationRewardedAdConfiguration?,
        mediationAdLoadCallback: MediationAdLoadCallback<MediationRewardedAd, MediationRewardedAdCallback>?
    ) {
        try {
            Log.d(TAG, "Enter loadRewardedAd")
            val adUnit: String? = mediationRewardedAdConfiguration?.serverParameters?.getString(
                MediationRewardedVideoAdAdapter.CUSTOM_EVENT_SERVER_PARAMETER_FIELD
            )
            val forwarder = HuaweiCustomEventRewardedAdEventForwarder(
                mediationRewardedAdConfiguration!!,
                mediationAdLoadCallback!!
            )
            forwarder.load(adUnit)
        } catch (exception: Throwable) {
            val stacktrace =
                StringWriter().also { exception.printStackTrace(PrintWriter(it)) }.toString().trim()
            Log.e(TAG, "Request Rewarded Ad Failed: $stacktrace")
        }

    }
}