package com.mycompany.advioo.repo

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mycompany.advioo.models.LocationSampleData
import com.mycompany.advioo.models.user.Driver
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val db: FirebaseFirestore
) : UserRepositoryInterface {

    private val usersCollection = db.collection("users")

    override fun uploadUserData(driver: Driver): Task<Void> {
        return usersCollection
            .document(driver.id)
            .set(driver, SetOptions.merge())
    }


    override fun getDriver(uid: String): Task<Driver?> {
        return usersCollection.document(uid).get()
            .continueWith { task ->
                if (task.isSuccessful) {
                    task.result?.toObject(Driver::class.java)
                } else {
                    null
                }
            }
    }

    override fun updateDriverCampaignStatus(
        userId: String,
        enrolledCampaignId: String
    ): Task<Void> {
        return usersCollection
            .document(userId)
            .update("currentEnrolledCampaign",enrolledCampaignId)
    }


    override fun uploadLocationData(userId: String, locationData: LocationSampleData): Task<Void> {
        return db.collection("location_data")
            .document()
            .set(locationData, SetOptions.merge())
    }


}