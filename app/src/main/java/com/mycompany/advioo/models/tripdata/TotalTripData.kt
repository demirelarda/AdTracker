package com.mycompany.advioo.models.tripdata


data class TotalTripData(
    val uploadDate: Long = 0L,
    val driverId: String = "",
    val tripDataList : List<UserTripData> = arrayListOf()
)
