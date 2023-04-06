package com.mycompany.advioo.util

import java.lang.Math.abs

class CalculateBounds(
    val nwLat: Double,
    val nwLng: Double,
    val seLat: Double,
    val seLng: Double
) {



    val centerLat = (nwLat + seLat) / 2
    val centerLng = (nwLng + seLng) / 2
    val latDelta = kotlin.math.abs(nwLat - seLat)
    val lngDelta = kotlin.math.abs(nwLng - seLng)
    val topLat = centerLat + (latDelta / 2)
    val bottomLat = centerLat - (latDelta / 2)
    val leftLng = centerLng - (lngDelta / 2)
    val rightLng = centerLng + (lngDelta / 2)

}