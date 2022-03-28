/*
 *   Copyright 2022. Huawei Technologies Co., Ltd. All rights reserved.
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

import androidx.annotation.IntDef
import com.google.android.gms.ads.AdError
import com.hmscl.huawei.admob_mediation.ErrorCode

object CustomEventError {
    const val SAMPLE_SDK_DOMAIN = "com.huawei.hms.ads"
    const val CUSTOM_EVENT_ERROR_DOMAIN = "com.hmscl.huawei.admob_mediation.all_ads"

    /** Error raised when the custom event adapter cannot obtain the ad unit id.  */
    const val ERROR_NO_AD_UNIT_ID = 101

    const val ERROR_AD_FETCH_FAILED=104

    /**
     * Error raised when the custom event adapter does not have an ad available when trying to show
     * the ad.
     */
    const val ERROR_AD_NOT_AVAILABLE = 102

    /** Error raised when the custom event adapter cannot obtain the activity context.  */
    const val ERROR_NO_ACTIVITY_CONTEXT = 103
    fun createCustomEventNoAdIdError(): AdError {
        return AdError(ERROR_NO_AD_UNIT_ID, "Ad unit id is empty", CUSTOM_EVENT_ERROR_DOMAIN)
    }

    fun createCustomEventAdNotAvailableError(): AdError {
        return AdError(ERROR_AD_NOT_AVAILABLE, "No ads to show", CUSTOM_EVENT_ERROR_DOMAIN)
    }

    fun createCustomEventNoActivityContextError(): AdError {
        return AdError(
            ERROR_NO_ACTIVITY_CONTEXT,
            "An activity context is required to show the sample ad",
            CUSTOM_EVENT_ERROR_DOMAIN
        )
    }

    /**
     * Creates a custom event `AdError`. This error wraps the underlying error thrown by the
     * HuaweiAds SDK.
     *
     * @param errorCode An `ErrorCode` to be reported.
     */
    fun createSampleSdkError(errorCode: ErrorCode): AdError {
        val message: String = errorCode.toString()
        return AdError(getMediationErrorCode(errorCode), message, SAMPLE_SDK_DOMAIN)
    }

    /**
     * Converts the SampleErrorCode to an integer in the range 0-99. This range is distinct from the
     * CustomEventErrorCode's range which is 100-199.
     *
     * @param errorCode the error code returned by HuaweiAds SDK
     * @return an integer in the range 0-99
     */
    private fun getMediationErrorCode(errorCode: ErrorCode): Int {
        return when (errorCode) {
            ErrorCode.UNKNOWN -> 0
            ErrorCode.BAD_REQUEST -> 1
            ErrorCode.NO_INVENTORY -> 2
            ErrorCode.NETWORK_ERROR -> 3
        }
        return 99
    }

    @IntDef(value = [ERROR_NO_AD_UNIT_ID, ERROR_AD_NOT_AVAILABLE, ERROR_NO_ACTIVITY_CONTEXT])
    annotation class SampleCustomEventErrorCode
}