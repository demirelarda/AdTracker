package com.mycompany.advioo.api

import com.mycompany.advioo.models.mail.EmailRequest
import com.mycompany.advioo.models.mail.EmailResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface MailjetAPI {
    @Headers("Content-Type: application/json")
    @POST("/v3.1/send")
    suspend fun sendEmail(
        @Header("Authorization") authHeader: String,
        @Body emailRequest: EmailRequest
    ): Response<EmailResponse>
}
