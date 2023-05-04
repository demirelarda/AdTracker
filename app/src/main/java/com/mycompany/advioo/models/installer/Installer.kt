package com.mycompany.advioo.models.installer

import android.os.Parcelable
import com.mycompany.advioo.models.campaign.LatLngPoint
import com.mycompany.advioo.models.user.UserCity
import kotlinx.parcelize.Parcelize

@Parcelize
data class Installer(
    var installerId: String = "",
    val installerEmail: String = "",
    val installerName: String = "",
    val installerAddress: String = "",
    val installerPhoneNumber: String = "",
    val installerLocation: UserCity = UserCity("","","",""),
    val installerCoordinates: LatLngPoint = LatLngPoint(),
    var stateId : String = "",
    var cityId: String = ""
):Parcelable