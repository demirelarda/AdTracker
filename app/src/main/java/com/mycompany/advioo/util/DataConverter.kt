package com.mycompany.advioo.util

import androidx.room.TypeConverter
import com.google.firebase.firestore.GeoPoint
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mycompany.advioo.models.MyPair
import com.mycompany.advioo.models.campaign.LatLngPoint
import java.lang.reflect.Type

class DataConverter {


    @TypeConverter
    fun fromLatLngPointList(latLngPoints: List<LatLngPoint>): String {
        val gson = Gson()
        return gson.toJson(latLngPoints)
    }

    @TypeConverter
    fun toLatLngPointList(latLngPointsString: String): List<LatLngPoint> {
        val gson = Gson()
        val objectType = object : TypeToken<List<LatLngPoint>>() {}.type
        return gson.fromJson(latLngPointsString, objectType)
    }

}
