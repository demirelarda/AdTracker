package com.mycompany.advioo.models.tripdata

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mycompany.advioo.models.MyPair

@Entity(tableName = "tripData")
data class UserTripData(
    @PrimaryKey val tripId: String = "",
    val sessionId: String = "",
    val campaignApplicationId: String = "",
    val driverId: String = "",
    val campaignId: String = "",
    val kmDriven: Double = 0.0,
    val earnedPayment: Double = 0.0,
    val locationPoints: ArrayList<MyPair> = arrayListOf(MyPair(0.0,0.0)),
    val localSaveDate: Long = 0L,
    var isUploaded: Boolean = false,
    val startTime: String = "",
    val endTime: String = "",
)

