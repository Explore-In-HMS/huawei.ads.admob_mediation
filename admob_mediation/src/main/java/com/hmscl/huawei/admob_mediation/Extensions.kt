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
import com.google.ads.consent.ConsentInformation
import com.google.ads.consent.ConsentStatus
import com.google.android.gms.ads.mediation.MediationAdConfiguration
import com.huawei.hms.ads.AdParam
import com.huawei.hms.ads.HwAds
import com.huawei.hms.ads.NonPersonalizedAd

fun MediationAdConfiguration.configureAdRequest(): AdParam {
    val tag = "MediationAdConf"
    Log.d(tag, "ExtensionFunctions - configureAdRequest()")
    val adParam = AdParam.Builder()

    /**
     * NPA-PA
     */
    try {
        val consentStatus: ConsentStatus =
            ConsentInformation.getInstance(this.context).consentStatus
        if (consentStatus == ConsentStatus.NON_PERSONALIZED)
            adParam.setNonPersonalizedAd(NonPersonalizedAd.ALLOW_NON_PERSONALIZED)
        else if (consentStatus == ConsentStatus.PERSONALIZED)
            adParam.setNonPersonalizedAd(NonPersonalizedAd.ALLOW_ALL)
    } catch (exception: Throwable) {
        Log.i(tag, "configureAdRequest: Consent status couldn't read")
    }

    /**
     * TCF2.0
     */
    try {
        val sharedPref = this.context.getSharedPreferences(
            "SharedPreferences",
            Context.MODE_PRIVATE
        )
        val tcfString = sharedPref?.getString("IABTCF_TCString", "")

        if (tcfString != null && tcfString != "") {
            val requestOptions = HwAds.getRequestOptions()
            requestOptions.toBuilder().setConsent(tcfString).build()
        }
    } catch (exception: Throwable) {
        Log.i(tag, "configureAdRequest: TCFString couldn't read")
    }

    /**
     * COPPA
     */
    adParam.setTagForChildProtection(this.taggedForChildDirectedTreatment())
    Log.d("TagforChildLog", this.taggedForChildDirectedTreatment().toString())

    return adParam.build()
}