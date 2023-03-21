package com.mycompany.advioo.models.user

import com.google.firebase.Timestamp
import com.google.type.Date


data class User(
    var id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val city: String,
    val addressFullName: String,
    val addressRow1: String,
    val addressRow2: String,
    val zipCode: String,
    val regDate: Timestamp,
    val carBrand: String,
    val carModel: String,
    val carYear: String,
    val carCondition: String,
    val licensePlate: String,
    val avgKmDriven: String,
    val workCity: String,
    val rideShareDriver: Boolean,
    val allowedContact: Boolean,
)

