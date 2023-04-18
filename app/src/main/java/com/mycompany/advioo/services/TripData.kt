package com.mycompany.advioo.services

import android.os.Parcelable
import com.google.firebase.firestore.GeoPoint
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue


@Parcelize
data class TripData(
    var distanceDriven: Double = 0.0,
    var accuracyList: MutableList<Pair<Float, @RawValue GeoPoint>> = mutableListOf(),
    var distancePoints: MutableList<Triple<@RawValue GeoPoint, Double, Long>> = mutableListOf(),
    var endPoint: @RawValue GeoPoint? = null,
    var startPoint: @RawValue GeoPoint? = null,
    var locationPoints: MutableList<@RawValue GeoPoint> = mutableListOf(),
    var startTime: Long = 0L,
    var endTime: Long = 0L,
    var userEmail: String? = null
) : Parcelable