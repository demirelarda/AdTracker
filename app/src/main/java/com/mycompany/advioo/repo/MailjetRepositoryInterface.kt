package com.mycompany.advioo.repo

import com.mycompany.advioo.models.mail.EmailRequest
import com.mycompany.advioo.models.mail.EmailResponse
import com.mycompany.advioo.util.Resource

interface MailjetRepositoryInterface {
    suspend fun sendEmail(authHeader: String, emailRequest: EmailRequest): Resource<EmailResponse>
}