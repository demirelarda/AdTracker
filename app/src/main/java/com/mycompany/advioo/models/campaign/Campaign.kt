package com.mycompany.advioo.models.campaign

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Campaign(
    var campaignId : String = "",
    var campaignTitle : String = "",
    var campaignImageURL : String = "",
    var city : String = "",
    var availableCampaignPlans : ArrayList<Int> = ArrayList(),
    var campaignLightPaymentRange : String = "",
    var campaignAdvancedPaymentRange : String = "",
    var campaignProPaymentRange : String = "",
    var campaignLeftTopCoordinate : String = "",
    var campaignRightTopCoordinate : String = "",
    var campaignLeftBottomCoordinate : String = "",
    var campaignRightBottomCoordinate : String = "",
    var campaignPaymentPerKM: Int = 0,
    var mapBorderLocationList: ArrayList<LatLngPoint> = ArrayList(),

) : Parcelable
