package com.mycompany.advioo.repo

import com.mycompany.advioo.models.datetime.ServerTime
import com.mycompany.advioo.util.Resource

interface TimeRepositoryInterface {
    suspend fun getTimeFromApi(location:String) : Resource<ServerTime>
}