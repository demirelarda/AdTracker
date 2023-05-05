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
)
