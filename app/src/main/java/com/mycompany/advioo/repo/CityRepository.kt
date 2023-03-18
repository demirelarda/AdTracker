package com.mycompany.advioo.repo

import com.mycompany.advioo.api.CityAPI
import com.mycompany.advioo.models.city.CityResponse
import com.mycompany.advioo.util.Resource
import javax.inject.Inject

class CityRepository @Inject constructor(
    private val cityAPI: CityAPI
): CityRepositoryInterface {


    override suspend fun getCities(): Resource<CityResponse> {
        return try{
            val response = cityAPI.getCities()
            if(response.isSuccessful){
                response.body()?.let {
                    return@let Resource.success(it)
                } ?: Resource.error("Error",null)
            }
            else{
                Resource.error("Error",null)
            }
        }
        catch (e:java.lang.Exception){
            Resource.error("No Data", null)
        }
    }

}