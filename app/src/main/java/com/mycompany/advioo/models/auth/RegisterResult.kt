package com.mycompany.advioo.models.auth

data class RegisterResult(
    val success: Boolean,
    val loading: Boolean,
    val error: Int?
)