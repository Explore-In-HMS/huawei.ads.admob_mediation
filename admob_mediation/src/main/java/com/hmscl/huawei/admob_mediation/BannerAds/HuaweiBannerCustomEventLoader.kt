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

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.doOnPreDraw
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdSize
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
        val isUnityApp: Boolean = try {
            Class.forName("com.unity3d.player.UnityPlayerActivity")
            true
        } catch (e: ClassNotFoundException) {
            false
        }

        if(context == null){
            mediationAdLoadCallback.onFailure(
                CustomEventError.createCustomEventNoActivityContextError()
            )
            return
        }
        this.context = mediationBannerAdConfiguration.context

        // All custom events have a server parameter named "parameter" that returns back the parameter
        // entered into the AdMob UI when defining the custom event.
        val serverParameter =
            mediationBannerAdConfiguration.serverParameters.getString("parameter")
        if (serverParameter.isNullOrBlank()) {
            mediationAdLoadCallback.onFailure(
                CustomEventError.createCustomEventNoAdIdError()
            )
            return
        }
        val context = mediationBannerAdConfiguration.context
        huaweiBannerView = BannerView(context)

        // Assumes that the serverParameter is the AdUnitId for Huawei Ads network.
        huaweiBannerView.adId = serverParameter

        val bannerAdSize = getHuaweiBannerAdSizeFromAdmobAdSize(
            mediationBannerAdConfiguration.adSize
        )
        huaweiBannerView.bannerAdSize = bannerAdSize

        if (isUnityApp) {
            (context as Activity).runOnUiThread {
                huaweiBannerView.visibility = View.INVISIBLE
            }
            huaweiBannerView.doOnPreDraw {
                Log.d(
                    TAG,
                    "BannerEventLoader - loadAd() - doOnPreDraw - Optimizing banner ad rendering for unity environment"
                )
                context.runOnUiThread {
                    if (bannerAdSize.width > 0) {
                        val displayMetrics = Resources.getSystem().displayMetrics
                        huaweiBannerView.layoutParams = FrameLayout.LayoutParams(
                            (bannerAdSize.width * displayMetrics.density).toInt(),
                            (bannerAdSize.height * displayMetrics.density).toInt()
                        )
                    } else {
                        if (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            val displayMetrics = Resources.getSystem().displayMetrics
                            val bannerWidth = displayMetrics.heightPixels
                            val bannerHeight = displayMetrics.widthPixels

                            val calculatedCoeff = if (bannerHeight >= bannerWidth) {
                                bannerHeight.toFloat() / bannerWidth
                            } else {
                                bannerWidth.toFloat() / bannerHeight
                            }
                            huaweiBannerView.layoutParams = FrameLayout.LayoutParams(
                                (it.width / calculatedCoeff).toInt(),
                                (it.height / calculatedCoeff).toInt()
                            )
                            Log.d(
                                TAG,
                                "BannerEventLoader - loadAd() - doOnPreDraw() - Calculated Coefficient:$calculatedCoeff, New width ${(it.width / calculatedCoeff).toInt()}, New Height: ${(it.height / calculatedCoeff).toInt()}"
                            )

                        } else {
                            huaweiBannerView.layoutParams =
                                FrameLayout.LayoutParams(it.width, it.height)
                        }
                    }
                    huaweiBannerView.visibility = View.VISIBLE
                }
            }
        }


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

    private fun getHuaweiBannerAdSizeFromAdmobAdSize(adSize: AdSize): BannerAdSize {

        var resultAdSize: BannerAdSize
        Log.d(
            TAG,
            "BannerEventLoader - getHuaweiBannerAdSizeFromAdmobAdSize() - Received ad size: $adSize"
        )

        if (adSize.isFullWidth && adSize.isAutoHeight) {
            Log.d(
                TAG,
                "BannerEventLoader - getHuaweiBannerAdSizeFromAdmobAdSize() - Calculated huawei banner size : BANNER_SIZE_SMART"
            )
            resultAdSize = BannerAdSize.BANNER_SIZE_SMART
        } else if (AdSize.BANNER == adSize || adSize == AdSize(320, 50)) {
            Log.d(
                TAG,
                "BannerEventLoader - getHuaweiBannerAdSizeFromAdmobAdSize() - Calculated huawei banner size : BANNER_SIZE_320_50"
            )
            resultAdSize = BannerAdSize.BANNER_SIZE_320_50
        } else if (AdSize.LARGE_BANNER == adSize) {
            Log.d(
                TAG,
                "BannerEventLoader - getHuaweiBannerAdSizeFromAdmobAdSize() - Calculated huawei banner size : BANNER_SIZE_320_100"
            )
            resultAdSize = BannerAdSize.BANNER_SIZE_320_100
        } else if (AdSize.MEDIUM_RECTANGLE == adSize) {
            Log.d(
                TAG,
                "BannerEventLoader - getHuaweiBannerAdSizeFromAdmobAdSize() - Calculated huawei banner size : BANNER_SIZE_300_250"
            )
            resultAdSize = BannerAdSize.BANNER_SIZE_300_250
        } else if (AdSize.FULL_BANNER == adSize) {
            Log.d(
                TAG,
                "BannerEventLoader - getHuaweiBannerAdSizeFromAdmobAdSize() - Calculated huawei banner size : BANNER_SIZE_468_60"
            )
            resultAdSize = BannerAdSize.BANNER_SIZE_468_60
        } else if (AdSize.LEADERBOARD == adSize) {
            Log.d(
                TAG,
                "BannerEventLoader - getHuaweiBannerAdSizeFromAdmobAdSize() - Calculated huawei banner size : BANNER_SIZE_728_90"
            )
            resultAdSize = BannerAdSize.BANNER_SIZE_728_90
        } else if (AdSize.WIDE_SKYSCRAPER == adSize) {
            Log.d(
                TAG,
                "BannerEventLoader - getHuaweiBannerAdSizeFromAdmobAdSize() - Calculated huawei banner size : BANNER_SIZE_160_600"
            )
            resultAdSize = BannerAdSize.BANNER_SIZE_160_600
        } else if (isFullWidthRequest(adSize)) {
            Log.d(
                TAG,
                "BannerEventLoader - getHuaweiBannerAdSizeFromAdmobAdSize() - Calculated huawei banner size : BANNER_SIZE_ADVANCED"
            )
            resultAdSize = BannerAdSize.BANNER_SIZE_ADVANCED
        } else {
            Log.d(
                TAG,
                "BannerEventLoader - getHuaweiBannerAdSizeFromAdmobAdSize() - Calculated huawei banner size : CustomAdvancedBannerSize"
            )
            resultAdSize = BannerAdSize.getCurrentDirectionBannerSize(context, adSize.width)
            if (resultAdSize == BannerAdSize.BANNER_SIZE_INVALID) {
                Log.d(
                    TAG,
                    "BannerEventLoader - getHuaweiBannerAdSizeFromAdmobAdSize() - Calculated huawei banner size : BANNER_SIZE_INVALID, Accepting custom banner ad size, New Banner size - Width:${adSize.width}, Height:${adSize.height}"
                )
                resultAdSize = BannerAdSize(adSize.width, adSize.height)
            }
        }

        return resultAdSize
    }

    private fun isFullWidthRequest(adSize: AdSize): Boolean {
        val displayMetrics = Resources.getSystem().displayMetrics

        val screenWidth = (displayMetrics.widthPixels / displayMetrics.density).toInt()
        return adSize.width == screenWidth
    }

    override fun getView(): View {
        return huaweiBannerView
    }
}