package com.mycompany.advioo.api

import com.mycompany.advioo.models.datetime.ServerTime
import com.mycompany.advioo.util.Util.TIME_API_AMERICA
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface TimeAPI {

    @GET("$TIME_API_AMERICA{location}")
    suspend fun getTimeFromAPI(@Path("location") location: String) : Response<ServerTime>

}