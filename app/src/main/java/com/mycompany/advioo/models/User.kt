package com.mycompany.advioo.models

import com.google.firebase.Timestamp
import com.google.type.Date


data class User(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val regDate: Timestamp = Timestamp.now(), // Set default value for regDate
    //val address: Address
)

