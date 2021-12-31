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

import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.mediation.customevent.CustomEventBannerListener
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitialListener
import com.huawei.hms.ads.AdListener
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.InterstitialAd
import com.huawei.hms.ads.banner.BannerView

class HuaweiCustomEventInterstitialEventForwarder(
        private var listener: CustomEventInterstitialListener,
        private var huaweiInterstitialView: InterstitialAd
) : AdListener() {
    override fun onAdLoaded() {
        listener.onAdLoaded()
    }

    override fun onAdFailed(errorCode: Int) {
        Log.e("TAG", "HuaweiCustomEventInterstitialEventForwarder = ${errorCode.toString()}")

        listener.onAdFailedToLoad(AdError(AdParam.ErrorCode.INNER, AdParam.ErrorCode.INNER.toString(),"HuaweiInterstitialAds"))
    }

    override fun onAdClosed() {
        listener.onAdClosed()
    }

    override fun onAdLeave() {
        listener.onAdLeftApplication()
    }

    override fun onAdOpened() {
        listener.onAdOpened()
    }

    override fun onAdClicked() {
        listener.onAdClicked()

    }

}