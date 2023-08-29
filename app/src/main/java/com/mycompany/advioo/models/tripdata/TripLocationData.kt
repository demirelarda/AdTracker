package com.mycompany.advioo.models.tripdata

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mycompany.advioo.models.campaign.LatLngPoint
@Entity(tableName = "tripLocationData")
data class TripLocationData(
    @PrimaryKey val tripId: String,
    val locationList: List<LatLngPoint>,
    val driverId: String,
    val campaignId: String,
    val date: Long
)
