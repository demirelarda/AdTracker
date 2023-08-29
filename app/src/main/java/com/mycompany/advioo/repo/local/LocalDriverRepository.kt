package com.mycompany.advioo.repo.local

import com.mycompany.advioo.dao.DriverDao
import com.mycompany.advioo.models.localuser.LocalDriver
import com.mycompany.advioo.models.tripdata.TripLocationData
import com.mycompany.advioo.models.tripdata.UserTripData
import javax.inject.Inject

class LocalDriverRepository @Inject constructor(
    private val driverDao: DriverDao,
) : LocalDriverRepositoryInterface{

    override suspend fun saveDriver(driver: LocalDriver) {
        driverDao.insertLocalDriver(driver)
    }

    override suspend fun getDriver(driverId: String): LocalDriver? {
        return driverDao.getLocalDriver(driverId)
    }

    override suspend fun deleteTripData(driverId: String) {
        return driverDao.deleteTripData(driverId)
    }

    override suspend fun updateDriverEnrolledCampaign(driverId: String, campaignId: String) {
        driverDao.updateEnrolledCampaigns(driverId,campaignId)
    }

    override suspend fun saveLocalTripData(tripData: UserTripData) {
        driverDao.insertLocalTripData(tripData)
    }



    override suspend fun getAllTripDataByUserId(userId: String): List<UserTripData> {
        return driverDao.getAllTripDataByUserId(userId)
    }

    override suspend fun getAllTripDataFromToday(
        dayStart: Long,
        dayEnd: Long,
        userId: String,
        campaignId: String): List<UserTripData> {
        return driverDao.getAllTripDataFromToday(dayStart,dayEnd,userId,campaignId)
    }

    override suspend fun getAllTripDataFromThisWeek(
        weekStart: Long,
        weekEnd: Long,
        userId: String,
        campaignId: String
    ): List<UserTripData> {
        return driverDao.getAllTripDataFromThisWeek(weekStart,weekEnd,userId,campaignId)
    }

    override suspend fun getAllTripDataFromThisMonth(
        monthStart: Long,
        monthEnd: Long,
        userId: String,
        campaignId: String
    ): List<UserTripData> {
        return driverDao.getAllTripDataFromThisMonth(monthStart,monthEnd,userId,campaignId)
    }

    override suspend fun getAllTripDataFromThisYear(
        yearStart: Long,
        yearEnd: Long,
        userId: String,
        campaignId: String
    ): List<UserTripData> {
        return driverDao.getAllTripDataFromThisYear(yearStart,yearEnd,userId,campaignId)
    }

    override suspend fun saveTripLocationData(tripLocationData: TripLocationData) {
       driverDao.insertTripLocationData(tripLocationData)
    }

    override suspend fun getAllTripLocationData(driverId: String) : List<TripLocationData>{
        return driverDao.getAllTripLocationDataByUserId(driverId)
    }

    override suspend fun deleteAllLocationList(driverId: String) {
        driverDao.deleteAllTripLocationData(driverId)
    }

    override suspend fun deleteSingleLocationList(tripId: String) {
        driverDao.deleteSingleTripLocationData(tripId)
    }



}