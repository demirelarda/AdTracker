package com.mycompany.advioo.models.campaign

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationModel(
    var state: String="",
    var city: String=""
) : Parcelable