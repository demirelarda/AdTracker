package com.mycompany.advioo.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mycompany.advioo.dao.DriverDao
import com.mycompany.advioo.models.localuser.LocalDriver


import androidx.room.TypeConverters
import com.mycompany.advioo.models.tripdata.UserTripData
import com.mycompany.advioo.util.DataConverter

@Database(entities = [LocalDriver::class, UserTripData::class], version = 1, exportSchema = false)
@TypeConverters(DataConverter::class)
abstract class UserDatabase : RoomDatabase(){

    abstract fun driverDao(): DriverDao

    companion object {

        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getDatabase(context: Context): UserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
