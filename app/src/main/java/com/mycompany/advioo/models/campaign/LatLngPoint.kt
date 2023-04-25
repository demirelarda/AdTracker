package com.mycompany.advioo.models.campaign

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LatLngPoint(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
):Parcelable
