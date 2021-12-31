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

import android.util.Log
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.mediation.customevent.CustomEventNativeListener

class HuaweiCustomEventNativeAdsEventForwarder (
    private val listener: CustomEventNativeListener,
    private val options: NativeAdOptions
) : HuaweiCustomEventNativeAdsListener() {
    override fun onAdClosed() {
        listener.onAdClosed()
        super.onAdClosed()
    }

    override fun onAdFailed(p0: Int) {
        listener.onAdFailedToLoad(p0)
        Log.d("TAG","HuaweiCustomEventNativeAdsEventForwarder = ${p0.toString()}")
        super.onAdFailed(p0)
    }

    override fun onAdLeave() {
        super.onAdLeave()
    }

    override fun onAdOpened() {
        listener.onAdOpened()
        super.onAdOpened()
    }

    override fun onAdLoaded() {
        super.onAdLoaded()
    }

    override fun onAdClicked() {
        listener.onAdClicked()
    }

    override fun onAdImpression() {
        super.onAdImpression()
    }
}