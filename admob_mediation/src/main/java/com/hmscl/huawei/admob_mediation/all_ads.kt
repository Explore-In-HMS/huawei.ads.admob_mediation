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
import android.util.Log
import androidx.annotation.Keep
import com.google.android.gms.ads.mediation.*
import com.hmscl.huawei.admob_mediation.BannerAds.HuaweiBannerCustomEventLoader
import com.hmscl.huawei.admob_mediation.InterstitialAds.HuaweiInterstitialCustomEventLoader
import com.hmscl.huawei.admob_mediation.NativeAds.HuaweiNativeCustomEventLoader
import com.hmscl.huawei.admob_mediation.RewardedAds.HuaweiRewardedCustomEventLoader

@Keep
class all_ads : Adapter() {
    private val TAG = all_ads::class.java.simpleName
    private var bannerLoader: HuaweiBannerCustomEventLoader? = null
    private var interstitialLoader: HuaweiInterstitialCustomEventLoader? = null
    private var rewardedLoader: HuaweiRewardedCustomEventLoader? = null
    private var nativeLoader: HuaweiNativeCustomEventLoader? = null
    override fun loadBannerAd(
        adConfiguration: MediationBannerAdConfiguration,
        callback: MediationAdLoadCallback<MediationBannerAd, MediationBannerAdCallback>
    ) {
        Log.d(TAG, "CustomAdapter - loadBannerAd()")

        bannerLoader = HuaweiBannerCustomEventLoader(adConfiguration, callback)
        bannerLoader!!.loadAd()
    }

    override fun loadInterstitialAd(
        adConfiguration: MediationInterstitialAdConfiguration,
        callback: MediationAdLoadCallback<MediationInterstitialAd, MediationInterstitialAdCallback>
    ) {
        Log.d(TAG, "CustomAdapter - loadInterstitialAd()")

        interstitialLoader = HuaweiInterstitialCustomEventLoader(adConfiguration, callback)
        interstitialLoader!!.loadAd()
    }

    override fun loadRewardedAd(
        mediationRewardedAdConfiguration: MediationRewardedAdConfiguration,
        mediationAdLoadCallback: MediationAdLoadCallback<MediationRewardedAd, MediationRewardedAdCallback>
    ) {
        Log.d(TAG, "CustomAdapter - loadRewardedAd()")

        rewardedLoader = HuaweiRewardedCustomEventLoader(
            mediationRewardedAdConfiguration, mediationAdLoadCallback
        )
        rewardedLoader!!.loadAd()
    }

    override fun loadNativeAd(
        adConfiguration: MediationNativeAdConfiguration,
        callback: MediationAdLoadCallback<UnifiedNativeAdMapper, MediationNativeAdCallback>
    ) {
        Log.d(TAG, "CustomAdapter - loadNativeAd()")

        nativeLoader = HuaweiNativeCustomEventLoader(adConfiguration, callback)
        nativeLoader!!.loadAd()
    }

    // This method won't be called for custom events.
    override fun initialize(
        context: Context,
        initializationCompleteCallback: InitializationCompleteCallback,
        list: List<MediationConfiguration>
    ) {
        return
    }

    override fun getVersionInfo(): VersionInfo {
        Log.d(TAG, "CustomAdapter - getVersionInfo()")

        val versionString: String = BuildConfigMediation.ADAPTER_VERSION
        val splits = versionString.split("\\.").toTypedArray()
        if (splits.size >= 4) {
            val major = splits[0].toInt()
            val minor = splits[1].toInt()
            val micro = splits[2].toInt() * 100 + splits[3].toInt()
            return VersionInfo(major, minor, micro)
        }
        return VersionInfo(0, 0, 0)
    }

    override fun getSDKVersionInfo(): VersionInfo {
        Log.d(TAG, "CustomAdapter - getSDKVersionInfo()")

        val versionString: String = BuildConfigMediation.ADS_SDK_VERSION
        val splits = versionString.split("\\.").toTypedArray()
        if (splits.size >= 3) {
            val major = splits[0].toInt()
            val minor = splits[1].toInt()
            val micro = splits[2].toInt()
            return VersionInfo(major, minor, micro)
        }
        return VersionInfo(0, 0, 0)
    }
}