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
import com.huawei.hms.ads.AdListener

open class HuaweiCustomEventNativeAdsListener : AdListener() {
    private var TAG = HuaweiCustomEventNativeAdsListener::class.java.simpleName
    override fun onAdClosed() {
        super.onAdClosed()
    }

    override fun onAdFailed(p0: Int) {
        Log.e(TAG, "HuaweiCustomEventNativeAdsListener = ${p0.toString()}")
        super.onAdFailed(p0)
    }

    override fun onAdLeave() {
        Log.d(TAG, "HuaweiCustomEventNativeAdsListener = oAdLeave()")
        super.onAdLeave()
    }

    override fun onAdOpened() {
        Log.d(TAG, "HuaweiCustomEventNativeAdsListener = oAdOpened()")
        super.onAdOpened()
    }

    override fun onAdLoaded() {
        Log.d(TAG, "HuaweiCustomEventNativeAdsListener = oAdLoaded")
        super.onAdLoaded()
    }

    override fun onAdClicked() {
        Log.d(TAG, "HuaweiCustomEventNativeAdsListener = oAdClicked()")
        super.onAdClicked()
    }

    override fun onAdImpression() {
        Log.d(TAG, "HuaweiCustomEventNativeAdsListener = oAdImpression()")
        super.onAdImpression()
    }
}