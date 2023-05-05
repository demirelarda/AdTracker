package com.mycompany.advioo.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mycompany.advioo.models.localuser.LocalDriver

@Dao
interface DriverDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocalDriver(localDriver: LocalDriver)

    @Query("SELECT * FROM drivers WHERE id = :driverId")
    suspend fun getLocalDriver(driverId: String): LocalDriver?

}
