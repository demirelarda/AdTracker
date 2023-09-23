package com.mycompany.advioo.repo


import com.google.android.gms.tasks.Task
import com.mycompany.advioo.models.CarImageDetails
import com.mycompany.advioo.models.ContactMessage
import com.mycompany.advioo.models.LocationSampleData
import com.mycompany.advioo.models.tripdata.TotalTripData
import com.mycompany.advioo.models.tripdata.TripLocationData
import com.mycompany.advioo.models.user.Driver

interface UserRepositoryInterface {

    fun uploadUserData(driver: Driver) : Task<Void>

    fun updateDriverData(driverId: String, updates: Map<String, Any>) : Task<Void>

    fun uploadLocationData(userId: String,locationData: LocationSampleData) : Task<Void>

    fun getDriver(uid: String): Task<Driver?>

    fun updateDriverCampaignStatus(userId: String, enrolledCampaignId: String) : Task<Void>

    fun uploadTripData(totalTripData: TotalTripData) : Task<Void>

    fun uploadTripLocationData(tripLocationData: List<TripLocationData>) : Task<Void>

    fun getAllUserTripData(userId: String) : Task<List<TotalTripData>>

    fun sendContactMessage(contactMessage: ContactMessage) : Task<Void>

    fun uploadCarPhotos(images: List<ByteArray>): Task<List<String>>

    fun uploadCarPhotoDetails(carImageDetails: CarImageDetails ):Task<Void>

    fun updateCampaignStatus(campaignApplicationId: String, updateValue: Int): Task<Void>


}