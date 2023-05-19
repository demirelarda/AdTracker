package com.mycompany.advioo.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DataConverter {

    @TypeConverter
    fun fromCampaignList(campaigns: List<String?>?): String? {
        if (campaigns == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<List<String?>?>() {}.type
        return gson.toJson(campaigns, type)
    }

    @TypeConverter
    fun toCampaignList(campaignsString: String?): List<String>? {
        if (campaignsString == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<List<String?>?>() {}.type
        return gson.fromJson<List<String>>(campaignsString, type)
    }
}
