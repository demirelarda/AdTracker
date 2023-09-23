package com.mycompany.advioo.models

import com.google.firebase.Timestamp


data class CarImageDetails(
    val userId: String,
    val userFullName: String,
    val sentTime: Timestamp,
    val campaignLevel: String,
    val campaignId: String,
    val installerId: String,
    val imageUrls: List<String>
)
