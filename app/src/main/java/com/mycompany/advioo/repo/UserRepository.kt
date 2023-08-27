package com.mycompany.advioo.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.mycompany.advioo.models.ContactMessage
import com.mycompany.advioo.models.LocationSampleData
import com.mycompany.advioo.models.campaign.Campaign
import com.mycompany.advioo.models.tripdata.TotalTripData
import com.mycompany.advioo.models.tripdata.UserTripData
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

    override fun updateDriverData(driverId: String, updates: Map<String, Any>): Task<Void> {
        return usersCollection
            .document(driverId)
            .update(updates)
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

    override fun uploadTripData(totalTripData: TotalTripData): Task<Void> {
        return db.collection("tripData")
            .document(totalTripData.driverId)
            .set(totalTripData, SetOptions.merge())
    }

    override fun getAllUserTripData(userId: String): Task<List<TotalTripData>> {
        val tripDataQuery = db.collection("tripData")
            .whereEqualTo("driverId", userId)

        return tripDataQuery.get().continueWith { task ->
            if (task.isSuccessful) {
                val result = task.result
                result?.documents?.map { it.toObject(TotalTripData::class.java)!! } ?: emptyList()
            } else {
                emptyList()
            }
        }
    }

    override fun sendContactMessage(contactMessage: ContactMessage): Task<Void> {
        return db.collection("userMessages")
            .document()
            .set(contactMessage, SetOptions.merge())
    }


    override fun uploadLocationData(userId: String, locationData: LocationSampleData): Task<Void> {
        return db.collection("location_data")
            .document()
            .set(locationData, SetOptions.merge())
    }




}