package com.mycompany.advioo.models.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserCity(
    val stateId: String="",
    val cityId: String="",
    val stateName: String="",
    val cityName: String="",
):Parcelable