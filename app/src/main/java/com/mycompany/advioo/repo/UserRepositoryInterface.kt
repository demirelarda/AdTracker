package com.mycompany.advioo.repo

import com.google.android.gms.tasks.Task
import com.mycompany.advioo.models.LocationSampleData
import com.mycompany.advioo.models.user.Driver

interface UserRepositoryInterface {

    fun uploadUserData(driver: Driver) : Task<Void>
    fun uploadLocationData(userId: String,locationData: LocationSampleData) : Task<Void>


}