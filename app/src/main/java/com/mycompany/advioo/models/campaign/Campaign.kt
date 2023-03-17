package com.mycompany.advioo.models.campaign

data class Campaign(
    var id: String,
    var title: String,
    var lightPrice: String,
    var advancedPrice: String,
    var proPrice: String,
    var dateRange: String,
    var location: String,
    var hotLocations: String,
    var city: String,
    var company: String,
)
