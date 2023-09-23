package com.mycompany.advioo.repo

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.mycompany.advioo.models.CarImageDetails
import com.mycompany.advioo.models.ContactMessage
import com.mycompany.advioo.models.LocationSampleData
import com.mycompany.advioo.models.tripdata.TotalTripData
import com.mycompany.advioo.models.tripdata.TripLocationData
import com.mycompany.advioo.models.user.Driver
import java.util.UUID
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
) : UserRepositoryInterface {

    private val usersCollection = db.collection("users")
    private val campaignApplicationsCollection = db.collection("campaignApplications")

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

    override fun uploadTripLocationData(tripLocationData: List<TripLocationData>): Task<Void> {
        val batch = db.batch()

        for (data in tripLocationData) {
            val documentRef = db.collection("tripLocationData").document(data.tripId)
            batch.set(documentRef, tripLocationDataToMap(data))
        }

        return batch.commit()
    }


    private fun tripLocationDataToMap(data: TripLocationData): Map<String, Any> {
        return mapOf(
            "tripId" to data.tripId,
            "locationList" to data.locationList.map {
                mapOf(
                    "latitude" to it.latitude,
                    "longitude" to it.longitude
                )
            },
            "driverId" to data.driverId,
            "campaignId" to data.campaignId,
            "date" to data.date
        )
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

    override fun uploadCarPhotos(images: List<ByteArray>): Task<List<String>> {
        val tasks: MutableList<Task<String>> = mutableListOf()

        for (image in images) {
            val fileName = "car_image_${System.currentTimeMillis().toString()+"_"+UUID.randomUUID()}.jpg"
            val fileRef = storage.reference.child("carImages/$fileName")

            val uploadTask = fileRef.putBytes(image).continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception!!
                }
                fileRef.downloadUrl
            }.continueWith { task ->
                task.result?.toString() ?: throw Exception("Download URL is null")
            }

            tasks.add(uploadTask)
        }

        return Tasks.whenAllSuccess<String>(tasks)
    }

    override fun uploadCarPhotoDetails(carImageDetails: CarImageDetails): Task<Void> {
        return db.collection("carPhotos")
            .document()
            .set(carImageDetails, SetOptions.merge())
    }

    override fun updateCampaignStatus(campaignApplicationId: String, updateValue: Int): Task<Void> {
        return campaignApplicationsCollection
            .document(campaignApplicationId)
            .update("status",updateValue)
    }


    override fun uploadLocationData(userId: String, locationData: LocationSampleData): Task<Void> {
        return db.collection("location_data")
            .document()
            .set(locationData, SetOptions.merge())
    }




}