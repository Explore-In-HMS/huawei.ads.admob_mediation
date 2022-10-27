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

package com.hmscl.huawei.admob_mediation.NativeAds

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.mediation.MediationAdLoadCallback
import com.google.android.gms.ads.mediation.MediationNativeAdCallback
import com.google.android.gms.ads.mediation.MediationNativeAdConfiguration
import com.google.android.gms.ads.mediation.UnifiedNativeAdMapper
import com.hmscl.huawei.admob_mediation.CustomEventError
import com.hmscl.huawei.admob_mediation.configureAdRequest
import com.huawei.hms.ads.AdListener
import com.huawei.hms.ads.VideoConfiguration
import com.huawei.hms.ads.nativead.NativeAd
import com.huawei.hms.ads.nativead.NativeAdConfiguration
import com.huawei.hms.ads.nativead.NativeAdLoader
import java.io.PrintWriter
import java.io.StringWriter


/** Native custom event loader for Huawei Ads SDK.  */
class HuaweiNativeCustomEventLoader(
    /** Configuration for requesting the native ad from the third party network.  */
    private val mediationNativeAdConfiguration: MediationNativeAdConfiguration,
    /** Callback that fires on loading success or failure.  */
    private val mediationAdLoadCallback: MediationAdLoadCallback<UnifiedNativeAdMapper, MediationNativeAdCallback>
) : NativeAd.NativeAdLoadedListener, AdListener() {
    private var context: Context? = null
    private val TAG = HuaweiNativeCustomEventLoader::class.java.simpleName
    private lateinit var mapper: HuaweiUnifiedNativeAdMapper

    /**
     * Callback for native ad events. The usual link/click tracking handled through callback methods
     * are handled through the GMA SDK*/

    private var mediationNativeAdCallback: MediationNativeAdCallback? = null

    /** Loads the native ad from Huawei Ads network.  */
    fun loadAd() {
        Log.d(TAG, "NativeEventLoader - loadAd()")
        this.context = mediationNativeAdConfiguration.context
        val serverParameter =
            mediationNativeAdConfiguration.serverParameters.getString("parameter").toString()

        if (serverParameter.isEmpty()) {
            Log.e(TAG, "Native serverParameter is empty or null")
        }
        try {
            val options = mediationNativeAdConfiguration.nativeAdOptions

            val videoConfiguration: VideoConfiguration
            val adConfiguration: NativeAdConfiguration

            if (options.videoOptions != null) {

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

            val builder = NativeAdLoader.Builder(context, serverParameter)
            val nativeAdLoader = builder.build()
            builder.setNativeAdOptions(adConfiguration)
            builder.setNativeAdLoadedListener { nativeAd ->
                if (!nativeAdLoader.isLoading) {
                    onNativeAdLoaded(nativeAd)
                }
            }.setAdListener(this)

            nativeAdLoader.loadAd(mediationNativeAdConfiguration.configureAdRequest())
        } catch (e: Exception) {

            val stacktrace =
                StringWriter().also { e.printStackTrace(PrintWriter(it)) }.toString().trim()
            Log.e(TAG, "Request Native Ad Failed: $stacktrace")
        }
    }

    override fun onNativeAdLoaded(native: NativeAd) {
        Log.d(TAG, "NativeEventLoader - onNativeAdLoaded()")
        mapper = context?.let { HuaweiUnifiedNativeAdMapper(native, it) }!!
        mediationNativeAdCallback =
            mediationAdLoadCallback.onSuccess(mapper as UnifiedNativeAdMapper)
    }

    override fun onAdClosed() {
        super.onAdClosed()
        Log.d(TAG, "NativeEventLoader - onAdClosed()")
    }

    override fun onAdFailed(p0: Int) {
        super.onAdFailed(p0)
        Log.e(
            TAG,
            "NativeEventLoader - onAdFailed() - Failed to load Huawei native with code: ${p0}."
        )
        mediationAdLoadCallback.onFailure(
            AdError(
                CustomEventError.ERROR_AD_FETCH_FAILED,
                "Ad fetch failed",
                CustomEventError.CUSTOM_EVENT_ERROR_DOMAIN
            )
        )
    }

    override fun onAdLeave() {
        super.onAdLeave()
        Log.d(TAG, "NativeEventLoader - onAdLeave()")
    }

    override fun onAdOpened() {
        super.onAdOpened()
        Log.d(TAG, "NativeEventLoader - onAdOpened()")
    }

    override fun onAdLoaded() {
        super.onAdLoaded()
        Log.d(TAG, "NativeEventLoader - onAdLoaded() - Ad loaded successfully")
    }

    override fun onAdClicked() {
        super.onAdClicked()
        Log.d(TAG, "NativeEventLoader - onAdClicked()")
    }

    override fun onAdImpression() {
        super.onAdImpression()
        Log.d(TAG, "NativeEventLoader - loadAd() - onAdImpression()")
    }
}