package com.mycompany.advioo.models.tripdata

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mycompany.advioo.models.MyPair
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "tripData")
data class UserTripData(
    @PrimaryKey val tripId: String = "",
    val campaignApplicationId: String = "",
    val driverId: String = "",
    val campaignId: String = "",
    val kmDriven: Double = 0.0,
    val earnedPayment: Double = 0.0,
    val localSaveDate: Long = 0L,
    var isUploaded: Boolean = false,
    val startTime: String = "",
    val endTime: String = "",
) : Parcelable


