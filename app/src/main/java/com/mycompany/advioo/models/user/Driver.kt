package com.mycompany.advioo.models.user

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Driver(
    var id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val city: String = "",
    val zipCode: String = "",
    val regDate: Timestamp = Timestamp.now(),
    val carBrand: String = "",
    val carModel: String = "",
    val carYear: String = "",
    val carCondition: String = "",
    val avgKmDriven: String = "",
    val rideShareDriver: Boolean = false,
    val allowedContact: Boolean = false,
    val userCity: UserCity = UserCity(),
    val currentEnrolledCampaign : String = "",
    val currentCampaignApplicationId: String = "",
    val driverPhoneNumber: String = ""
):Parcelable

