package com.mycompany.advioo.repo


import com.mycompany.advioo.models.pinfo.PinfoResponse
import com.mycompany.advioo.util.Resource

interface PinfoRepositoryInterface {
    suspend fun getPinfos() : Resource<PinfoResponse>
}