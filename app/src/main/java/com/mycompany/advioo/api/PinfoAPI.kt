package com.mycompany.advioo.api

import com.mycompany.advioo.models.pinfo.PinfoResponse
import com.mycompany.advioo.util.Util.GET_PINFO
import retrofit2.Response
import retrofit2.http.GET

interface PinfoAPI {

    @GET(GET_PINFO)
    suspend fun getPinfos(): Response<PinfoResponse>

}