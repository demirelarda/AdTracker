package com.mycompany.advioo.models

import com.google.firebase.Timestamp


data class ContactMessage(
    val userId: String,
    val userEmail: String,
    val userFullName: String,
    val sentTime: Timestamp,
    val title: String,
    val message: String,
    val isSolved: Boolean
)
