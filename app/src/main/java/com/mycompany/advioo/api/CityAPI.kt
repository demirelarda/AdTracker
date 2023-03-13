package com.mycompany.advioo.api

import com.mycompany.advioo.models.city.CityResponse
import com.mycompany.advioo.util.Util.GET_CITIES
import retrofit2.Response
import retrofit2.http.GET


interface CityAPI {

    @GET(GET_CITIES)
    suspend fun getCities(
    ) : Response<CityResponse>

}