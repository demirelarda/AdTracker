package com.mycompany.advioo.services

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationClient {
    var previousLocation: Location?
    fun getLocationUpdates(interval: Long): Flow<Location>

    class LocationException(message: String) : Exception()
}