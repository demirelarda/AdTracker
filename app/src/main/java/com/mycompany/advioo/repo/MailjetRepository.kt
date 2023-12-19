package com.mycompany.advioo.repo

import com.mycompany.advioo.api.MailjetAPI
import com.mycompany.advioo.models.mail.EmailRequest
import com.mycompany.advioo.models.mail.EmailResponse
import com.mycompany.advioo.util.Resource
import javax.inject.Inject

class MailjetRepository @Inject constructor(
    private val mailjetAPI: MailjetAPI
): MailjetRepositoryInterface {
    override suspend fun sendEmail(authHeader: String, emailRequest: EmailRequest): Resource<EmailResponse> {
        return try {
            val response = mailjetAPI.sendEmail(authHeader, emailRequest)
            if (response.isSuccessful) {
                println("error in api =m ${response.body().toString()}")
                println("error in api = ${response.code().toString()}")
                response.body()?.let {
                    return@let Resource.success(it)
                } ?: Resource.error("Error", null)
            } else {
                println("error in api = ${response.body().toString()}")
                println("error in api = ${response.code().toString()}")
                Resource.error("Error", null)
            }
        } catch (e: Exception) {
            println("error in api = ${e.localizedMessage}")
            Resource.error("No Data", null)
        }
    }
}