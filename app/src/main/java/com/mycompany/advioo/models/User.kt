package com.mycompany.advioo.models

data class User(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val address: Address

)