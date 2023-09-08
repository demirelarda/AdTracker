package com.mycompany.advioo.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mycompany.advioo.models.localuser.LocalDriver
import com.mycompany.advioo.models.tripdata.TripLocationData
import com.mycompany.advioo.models.tripdata.UserTripData
import com.mycompany.advioo.services.TripData

@Dao
interface DriverDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocalDriver(localDriver: LocalDriver)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocalTripData(userTripData: UserTripData)

    @Query("SELECT * FROM drivers WHERE id = :driverId")
    suspend fun getLocalDriver(driverId: String): LocalDriver?

    @Query("UPDATE drivers SET currentEnrolledCampaign = :campaignId WHERE id = :driverId")
    suspend fun updateEnrolledCampaigns(driverId: String, campaignId: String)

    @Query("SELECT * FROM tripData WHERE tripId = :tripId")
    suspend fun getTripData(tripId: String) : UserTripData?

    @Query("DELETE FROM tripData WHERE driverId = :userId")
    suspend fun deleteTripData(userId: String)

    @Query("SELECT * FROM tripData WHERE driverId = :userId")
    suspend fun getAllTripDataByUserId(userId: String) : List<UserTripData>

    @Query("SELECT * FROM tripData WHERE localSaveDate BETWEEN :dayStart AND :dayEnd AND driverId = :userId AND campaignId = :campaignID")
    suspend fun getAllTripDataFromToday(dayStart: Long, dayEnd: Long, userId: String, campaignID: String): List<UserTripData>

    @Query("SELECT * FROM tripData WHERE localSaveDate BETWEEN :weekStart AND :weekEnd AND driverId = :userId AND campaignId = :campaignID")
    suspend fun getAllTripDataFromThisWeek(weekStart: Long, weekEnd: Long, userId: String, campaignID: String): List<UserTripData>

    @Query("SELECT * FROM tripData WHERE localSaveDate BETWEEN :monthStart AND :monthEnd AND driverId = :userId AND campaignId = :campaignID")
    suspend fun getAllTripDataFromThisMonth(monthStart: Long, monthEnd: Long, userId: String, campaignID: String): List<UserTripData>

    @Query("SELECT * FROM tripData WHERE localSaveDate BETWEEN :yearStart AND :yearEnd AND driverId = :userId AND campaignId = :campaignID")
    suspend fun getAllTripDataFromThisYear(yearStart: Long, yearEnd: Long, userId: String, campaignID: String): List<UserTripData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTripLocationData(locationData: TripLocationData)

    @Query("DELETE FROM tripLocationData WHERE tripId = :tripID ")
    suspend fun deleteSingleTripLocationData(tripID: String)

    @Query("DELETE FROM tripLocationData WHERE driverId = :driverID")
    suspend fun deleteAllTripLocationData(driverID: String)

    @Query("SELECT * FROM tripLocationData WHERE driverId = :driverID AND date >= :currentTime - 3600000")
    suspend fun getTripLocationDataFromLastHour(driverID: String, currentTime: Long) : List<TripLocationData>


    @Query("SELECT * FROM tripLocationData WHERE driverId = :userId")
    suspend fun getAllTripLocationDataByUserId(userId: String) : List<TripLocationData>


}
