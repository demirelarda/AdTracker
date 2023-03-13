package com.mycompany.advioo.repo

import com.mycompany.advioo.models.city.CityResponse
import com.mycompany.advioo.util.Resource

interface CityRepositoryInterface {
    suspend fun getCities() : Resource<CityResponse>
}