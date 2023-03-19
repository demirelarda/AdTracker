package com.mycompany.advioo.models.city

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Province(
    val cities: List<String>,
    val stateName: String
):Parcelable