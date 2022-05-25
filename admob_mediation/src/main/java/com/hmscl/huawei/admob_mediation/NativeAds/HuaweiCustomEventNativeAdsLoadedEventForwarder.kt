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
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.mediation.customevent.CustomEventNativeListener
import com.huawei.hms.ads.nativead.NativeAd

class HuaweiCustomEventNativeAdsLoadedEventForwarder(
    private val listener: CustomEventNativeListener,
    private val options: NativeAdOptions,
    private val context: Context
) : HuaweiCustomEventNativeAdsLoadedListener() {
    override fun onNativeAdLoaded(native: NativeAd) {
        val mapper = HuaweiCustomEventNativeAdsMapper(native, context)
        listener.onAdLoaded(mapper)
    }
}