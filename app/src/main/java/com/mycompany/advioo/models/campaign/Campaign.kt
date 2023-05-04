package com.mycompany.advioo.models.campaign

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.mycompany.advioo.models.user.UserCity
import kotlinx.parcelize.Parcelize
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
data class Campaign(
    var campaignId: String = "",
    var campaignTitle: String = "",
    var campaignImageURL: String = "",
    var city: String = "",
    var selectedCities : ArrayList<UserCity> = ArrayList(),
    var availableCampaignPlans: ArrayList<Int> = ArrayList(),
    var mapBorderLocationList: ArrayList<LatLngPoint> = ArrayList(),
    var campaignMinKM : Int = 0,
    var campaignMaxKM : Int = 0,
    var campaignLightPaymentRange: String = "",
    var campaignAdvPaymentRange: String = "",
    var campaignProPaymentRange: String = "",
    var addedOn: Timestamp = Timestamp(Date()),
    var totalPaymentRange: String = "",
    var enrolledUserIdLevelMap: ArrayList<Pair<String,String>> = ArrayList(),

) : Parcelable
