package com.mycompany.advioo.repo

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mycompany.advioo.models.LocationSampleData
import com.mycompany.advioo.models.user.Driver
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val db: FirebaseFirestore
): UserRepositoryInterface {

    override fun uploadUserData(driver: Driver): Task<Void> {
        return db.collection("users")
            .document(driver.id)
            .set(driver, SetOptions.merge())
    }

    override fun uploadLocationData(userId: String,locationData:LocationSampleData): Task<Void> {
        return db.collection("location_data")
            .document()
            .set(locationData, SetOptions.merge())
    }


}