package com.mycompany.advioo.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mycompany.advioo.dao.CampaignApplicationDao
import com.mycompany.advioo.dao.DriverDao
import com.mycompany.advioo.models.localapplication.LocalCampaignApplication
import com.mycompany.advioo.models.localuser.LocalDriver
import com.mycompany.advioo.util.DataTypeConverters


@Database(entities = [LocalDriver::class, LocalCampaignApplication::class], version = 1, exportSchema = false)
@TypeConverters(DataTypeConverters::class)
abstract class UserDatabase : RoomDatabase(){

    abstract fun driverDao(): DriverDao
    abstract fun campaignApplicationDao(): CampaignApplicationDao

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