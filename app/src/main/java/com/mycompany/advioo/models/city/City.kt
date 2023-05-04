package com.mycompany.advioo.models.city

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class City(
    val id: String,
    val name: String
):Parcelable
