package com.mycompany.advioo.models.mail

data class EmailRequest(
    val Messages: List<Message>
)

data class Message(
    val From: EmailUser,
    val To: List<EmailUser>,
    val Subject: String,
    //val TextPart: String,
    val HTMLPart: String
)

data class EmailUser(
    val Email: String,
    val Name: String
)

data class EmailResponse(
    val Messages: List<EmailStatus>
)

data class EmailStatus(
    val Status: String,
    val To: List<EmailResult>
)

data class EmailResult(
    val Email: String,
    val MessageUUID: String,
    val MessageID: String,
    val MessageHref: String
)
