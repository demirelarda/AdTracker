package com.mycompany.advioo.models.localuser

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drivers")
data class LocalDriver(
    @PrimaryKey val id: String,
    val name: String,
    val surname: String,
    val email: String,
    val stateId: String,
    val cityId: String,
    val stateName: String,
    val cityName: String,
    val currentEnrolledCampaign: String,
    val city: String,
    val addressRow1: String,
    val addressRow2: String,
    val zipCode: String,
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
