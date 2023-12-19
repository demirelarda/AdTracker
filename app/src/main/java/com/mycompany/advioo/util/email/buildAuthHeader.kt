package com.mycompany.advioo.util.email

import android.util.Base64

object MailHelper{
    fun buildAuthHeader(apiKeyPublic: String, apiKeyPrivate: String): String {
        val credentials = "$apiKeyPublic:$apiKeyPrivate"
        val encodedCredentials = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
        return "Basic $encodedCredentials"
    }
}

