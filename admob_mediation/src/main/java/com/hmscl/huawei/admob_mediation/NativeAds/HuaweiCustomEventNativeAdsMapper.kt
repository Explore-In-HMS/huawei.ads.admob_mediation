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
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.google.android.gms.ads.mediation.UnifiedNativeAdMapper
import com.hmscl.huawei.admob_mediation.NativeAds.HuaweiCustomEventNativeAdsImageMapper
import com.huawei.hms.ads.nativead.NativeAd

class HuaweiCustomEventNativeAdsMapper(
    private var huaweiNativeAd: NativeAd,
    private val context: Context
) : UnifiedNativeAdMapper() {

    init {
        if (huaweiNativeAd.choicesInfo.content != "" && huaweiNativeAd.choicesInfo.icons.size > 0) {
            val whyThisAd: Button = Button(context)
            whyThisAd.setCompoundDrawables(
                huaweiNativeAd.choicesInfo.icons[0].drawable,
                null,
                null,
                null
            )
            whyThisAd.text = huaweiNativeAd.choicesInfo.content
            whyThisAd.setOnClickListener { huaweiNativeAd.gotoWhyThisAdPage(context) }
            adChoicesContent = whyThisAd
        }
        advertiser = huaweiNativeAd.adSource
        body = huaweiNativeAd.description
        callToAction = huaweiNativeAd.callToAction
        extras = huaweiNativeAd.extraBundle
        headline = huaweiNativeAd.title

        if (huaweiNativeAd.icon != null) {
            icon = HuaweiCustomEventNativeAdsImageMapper(huaweiNativeAd.icon)
        }

        if (huaweiNativeAd.images != null) {
            val imagesList = mutableListOf<com.google.android.gms.ads.formats.NativeAd.Image>()
            for (image in huaweiNativeAd.images) {
                imagesList.add(HuaweiCustomEventNativeAdsImageMapper(image))
            }
            images = imagesList
        }

        if (huaweiNativeAd.mediaContent != null) {
            setMediaView(huaweiNativeAd.mediaContent as View)
            setHasVideoContent(huaweiNativeAd.videoOperator.hasVideo())
            mediaContentAspectRatio = huaweiNativeAd.mediaContent.aspectRatio
        }

        overrideClickHandling = false
        overrideImpressionRecording = false
        price = huaweiNativeAd.price
        starRating = huaweiNativeAd.rating
        store = huaweiNativeAd.market
    }

    override fun recordImpression() {
        huaweiNativeAd.recordImpressionEvent(extras)
    }

    override fun handleClick(view: View?) {
        // recordClickEvent will be called automatically when triggerClick called.
        huaweiNativeAd.triggerClick(extras)
    }
}