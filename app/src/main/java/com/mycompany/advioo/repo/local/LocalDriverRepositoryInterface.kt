package com.mycompany.advioo.repo.local


import com.mycompany.advioo.models.localuser.LocalDriver
import com.mycompany.advioo.models.tripdata.UserTripData

interface LocalDriverRepositoryInterface {

    suspend fun saveDriver(driver: LocalDriver)

    suspend fun getDriver(driverId: String): LocalDriver?

    suspend fun deleteTripData(driverId: String)

    suspend fun updateDriverEnrolledCampaign(driverId: String,campaignId: String)

    suspend fun saveLocalTripData(tripData: UserTripData)

    suspend fun getAllTripDataByUserId(userId: String) : List<UserTripData>

    suspend fun getAllTripDataFromToday(dayStart: Long, dayEnd: Long, userId: String, campaignId: String): List<UserTripData>

    suspend fun getAllTripDataFromThisWeek(weekStart: Long, weekEnd: Long, userId: String, campaignId: String): List<UserTripData>

    suspend fun getAllTripDataFromThisMonth(monthStart: Long, monthEnd: Long, userId: String, campaignId: String): List<UserTripData>

    suspend fun getAllTripDataFromThisYear(yearStart: Long, yearEnd: Long, userId: String, campaignId: String): List<UserTripData>


}