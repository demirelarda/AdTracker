package com.mycompany.advioo.models.city

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Province(
    val id: String,
    val stateName: String,
    val cities: List<City>,
    val timezone: String
):Parcelable