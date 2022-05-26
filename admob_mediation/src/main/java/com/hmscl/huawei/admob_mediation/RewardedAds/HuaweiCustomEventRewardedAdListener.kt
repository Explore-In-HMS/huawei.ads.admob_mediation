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

package com.hmscl.huawei.admob_mediation.RewardedAds

import android.util.Log
import com.huawei.hms.ads.reward.RewardAdLoadListener

open class HuaweiCustomEventRewardedAdListener : RewardAdLoadListener() {
    private val TAG = HuaweiCustomEventRewardedAdListener::class.java.simpleName

    override fun onRewardAdFailedToLoad(errorCode: Int) {
        super.onRewardAdFailedToLoad(errorCode)
        Log.e(TAG, "HuaweiCustomEventRewardedAdListener = ${errorCode.toString()}")
    }

    override fun onRewardedLoaded() {
        Log.d(TAG, "HuaweiCustomEventRewardedAdListener = onRewardedLoaded()")
        super.onRewardedLoaded()
    }
}