package com.mycompany.advioo.repo

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mycompany.advioo.models.LocationSampleData
import com.mycompany.advioo.models.user.User
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val db: FirebaseFirestore
): UserRepositoryInterface {

    override fun uploadUserData(user: User): Task<Void> {
        return db.collection("users")
            .document(user.id)
            .set(user, SetOptions.merge())
    }

    override fun uploadLocationData(userId: String,locationData:LocationSampleData): Task<Void> {
        return db.collection("location_data")
            .document()
            .set(locationData, SetOptions.merge())
    }


}