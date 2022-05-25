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

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.mediation.customevent.CustomEventInterstitialListener
import com.huawei.hms.ads.AdListener

class HuaweiCustomEventInterstitialEventForwarder(
    private var listener: CustomEventInterstitialListener
) : AdListener() {
    private val TAG = HuaweiCustomEventInterstitialEventForwarder::class.java.simpleName
    private val handler = Handler(Looper.getMainLooper())

    override fun onAdLoaded() {
        Log.d(TAG, "HuaweiCustomEventInterstitialEventForwarder =  onAdLoaded()")
        handler.post {
            listener.onAdLoaded()
        }
    }

    override fun onAdFailed(errorCode: Int) {
        Log.e(TAG, "HuaweiCustomEventInterstitialEventForwarder = $errorCode")
        handler.post {
            listener.onAdFailedToLoad(AdError(errorCode, "HuaweiInterstitialAds", "onAdFailed()"))
        }
    }

    override fun onAdClosed() {
        Log.d(TAG, "HuaweiCustomEventInterstitialEventForwarder =  onAdClosed()")
        handler.post {
            listener.onAdClosed()
        }
    }

    override fun onAdLeave() {
        Log.d(TAG, "HuaweiCustomEventInterstitialEventForwarder =  onAdLeave()")
        handler.post {
            listener.onAdLeftApplication()
        }
    }

    override fun onAdOpened() {
        Log.d(TAG, "HuaweiCustomEventInterstitialEventForwarder =  onAdOpened()")
        handler.post {
            listener.onAdOpened()
        }
    }

    override fun onAdClicked() {
        Log.d(TAG, "HuaweiCustomEventInterstitialEventForwarder =  onAdClicked()")
        handler.post {
            listener.onAdClicked()
        }
    }
}