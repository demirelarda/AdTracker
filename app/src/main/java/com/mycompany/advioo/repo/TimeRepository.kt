package com.mycompany.advioo.repo

import com.mycompany.advioo.api.TimeAPI
import com.mycompany.advioo.models.datetime.ServerTime
import com.mycompany.advioo.util.Resource
import javax.inject.Inject

class TimeRepository @Inject constructor(
    private val timeAPI: TimeAPI
) : TimeRepositoryInterface {

    override suspend fun getTimeFromApi(location: String): Resource<ServerTime> {
        return try {
            val response = timeAPI.getTimeFromAPI(location)
            if (response.isSuccessful) {
                response.body()?.let {
                    return@let Resource.success(it)
                } ?: Resource.error("Error: Empty body", null)
            } else {
                Resource.error("Error: ${response.errorBody()?.string()}", null)
            }
        } catch (e: Exception) {
            Resource.error("No Data: ${e.message}", null)
        }


    }
}