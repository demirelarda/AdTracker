package com.mycompany.advioo.models.payment

data class PaymentRequest(
    val requestId: String,
    val driverId: String,
    val driverName: String,
    val currentCampaignId: String,
    val amount: Double,
    val bankAccountNumber: String,
    val bankTransitNumber: String,
    val bankInstitutionNumber: String,
    val imageUrls: List<String>,
    val currentCampaignApplicationId: String,
    val paymentFormFullName: String
)
