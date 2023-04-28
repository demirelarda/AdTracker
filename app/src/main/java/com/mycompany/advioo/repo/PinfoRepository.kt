package com.mycompany.advioo.repo


import com.mycompany.advioo.api.PinfoAPI
import com.mycompany.advioo.models.pinfo.PinfoResponse
import com.mycompany.advioo.util.Resource
import javax.inject.Inject

class PinfoRepository @Inject constructor(
    private val pinfoAPI: PinfoAPI
) : PinfoRepositoryInterface {

    override suspend fun getPinfos(): Resource<PinfoResponse> {
        return try {
            val response = pinfoAPI.getPinfos()
            if (response.isSuccessful) {
                response.body()?.let {
                    return@let Resource.success(it)
                } ?: Resource.error("Error", null)
            } else {
                Resource.error("Error", null)
            }
        } catch (e: java.lang.Exception) {
            Resource.error("No Data", null)
        }
    }

}