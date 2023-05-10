package com.mycompany.advioo.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.mycompany.advioo.models.campaign.Campaign
import com.mycompany.advioo.models.installer.Installer

object DataTypeConverters {
    private val gson = Gson()

    // Campaign Converters
    @TypeConverter
    @JvmStatic
    fun fromCampaign(value: Campaign): String {
        return gson.toJson(value)
    }

    @TypeConverter
    @JvmStatic
    fun toCampaign(value: String): Campaign {
        return gson.fromJson(value, Campaign::class.java)
    }

    // Installer Converters
    @TypeConverter
    @JvmStatic
    fun fromInstaller(value: Installer): String {
        return gson.toJson(value)
    }

    @TypeConverter
    @JvmStatic
    fun toInstaller(value: String): Installer {
        return gson.fromJson(value, Installer::class.java)
    }
}

