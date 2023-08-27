package com.mycompany.advioo.util

import androidx.room.TypeConverter
import com.google.firebase.firestore.GeoPoint
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mycompany.advioo.models.MyPair
import java.lang.reflect.Type

class DataConverter {



    @TypeConverter
    fun fromLocationPointsList(locationPoints: ArrayList<MyPair>): String {
        val gson = Gson()
        return gson.toJson(locationPoints)
    }

    @TypeConverter
    fun toLocationPointsList(locationPointsString: String): ArrayList<MyPair> {
        val gson = Gson()
        val objectType = object : TypeToken<ArrayList<MyPair>>() {}.type
        return gson.fromJson(locationPointsString, objectType)
    }

}
