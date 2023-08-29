package com.mycompany.advioo.viewmodels

import android.widget.Toast
import androidx.lifecycle.*
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.type.DateTime
import com.mycompany.advioo.models.campaign.Campaign
import com.mycompany.advioo.models.localuser.LocalDriver
import com.mycompany.advioo.models.tripdata.TotalTripData
import com.mycompany.advioo.models.tripdata.TripLocationData
import com.mycompany.advioo.models.tripdata.UserTripData
import com.mycompany.advioo.models.user.Driver
import com.mycompany.advioo.repo.CampaignRepositoryInterface
import com.mycompany.advioo.repo.UserRepositoryInterface
import com.mycompany.advioo.repo.local.LocalDriverRepositoryInterface
import com.mycompany.advioo.services.TripData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: CampaignRepositoryInterface,
    private val localDriverRepository: LocalDriverRepositoryInterface,
    private val auth: FirebaseAuth,
    private val userRepository: UserRepositoryInterface
) : ViewModel() {

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean>
        get() = _loadingState

    private val _failState = MutableLiveData<Boolean>()
    val failState: LiveData<Boolean>
        get() = _failState

    private val _saveDriverSuccess = MutableLiveData<Boolean>()
    val saveDriverSuccess: LiveData<Boolean>
        get() = _saveDriverSuccess

    private val _saveDriverFailure = MutableLiveData<Throwable?>()
    val saveDriverFailure: LiveData<Throwable?>
        get() = _saveDriverFailure

    private val _triggerCampaigns = MutableLiveData<Unit>()

    private val _userObject = MutableLiveData<LocalDriver?>()
    val userObject: LiveData<LocalDriver?>
        get() = _userObject

    private val _tripDataList = MutableLiveData<List<UserTripData>>()

    private val _tripLocationData = MutableLiveData<List<TripLocationData>>()

    val campaigns: LiveData<List<Campaign>> = _triggerCampaigns.switchMap {
        liveData {
            _loadingState.value = true
            val localUser = localDriverRepository.getDriver(auth.uid!!)
            val stateId = localUser?.stateId
            if (localUser != null) {
                println("used local database for user")
                println("state id = $stateId")
                _userObject.value = localUser
                emitSource(repository.getCampaigns(localUser.stateId))
            } else {
                //_failState.value = true
                getUserFromFirestore()
            }
            _loadingState.value = false
        }
    }

    init {
        _triggerCampaigns.value = Unit
    }

    private fun getUserFromFirestore() {
        println("call from firestore to get user object ")
        _loadingState.value = true
        userRepository.getDriver(auth.uid!!)
            .addOnSuccessListener { driver ->
                val localDriver = LocalDriver(
                    id = driver!!.id,
                    name = driver.firstName,
                    surname = driver.lastName,
                    email = driver.email,
                    stateId = driver.userCity.stateId,
                    cityId = driver.userCity.cityId,
                    stateName = driver.userCity.stateName,
                    cityName = driver.userCity.cityName,
                    currentEnrolledCampaign = driver.currentEnrolledCampaign,
                    city = driver.city,
                    addressRow1 = driver.addressRow1,
                    addressRow2 = driver.addressRow2,
                    carModel = driver.carModel,
                    carCondition = driver.carCondition,
                    carYear = driver.carYear,
                    licensePlate = driver.licensePlate,
                    avgKmDriven = driver.avgKmDriven,
                    workCity = driver.workCity,
                    rideShareDriver = driver.rideShareDriver,
                    allowedContact = driver.allowedContact,
                    zipCode = driver.zipCode,
                    carBrand = driver.carBrand
                )
                _userObject.value = localDriver
                saveDriver(localDriver)

            }
            .addOnFailureListener {
                println("error getting user data = " + it.localizedMessage)
                _loadingState.value = false
                _failState.value = true
            }
    }


    private fun uploadTripDataIfNecessary() {
        println("uploading trip data")
        if (_tripDataList.value != null) {
            val tripDataList = _tripDataList.value!!
            val newTripDataList = tripDataList.toMutableList()
            for (data in tripDataList) {
                if (data.isUploaded) {
                    newTripDataList.remove(data)
                }
            }
            val sortedTripDataList = newTripDataList.sortedByDescending { it.localSaveDate }
            println("last local save date = " + sortedTripDataList[0])
            if (isOneHourPassed(sortedTripDataList[0].localSaveDate)) {
                println("one hour passed")
                val newSortedTripDataList = sortedTripDataList.toMutableList()
                for (data in sortedTripDataList) {
                    if (!(data.isUploaded)) {
                        data.isUploaded = true
                        newTripDataList.add(data)
                    }
                }
                newTripDataList.sortedByDescending { it.localSaveDate }
                val totalTripData = TotalTripData(
                    uploadDate = System.currentTimeMillis(),
                    driverId = auth.uid!!,
                    tripDataList = tripDataList
                )
                userRepository.uploadTripData(totalTripData).addOnSuccessListener {
                    println("uploaded trip data successfuly")
                    userRepository.getAllUserTripData(auth.uid!!).addOnSuccessListener {
                        viewModelScope.launch {
                            localDriverRepository.deleteTripData(auth.uid!!)
                            for (tripData in it[0].tripDataList) {
                                try {
                                    localDriverRepository.saveLocalTripData(tripData)
                                    getAllTripLocationDataFromLocal()
                                } catch (e: Exception) {
                                    _loadingState.value = false
                                    _failState.value = true
                                }
                            }
                        }
                    }.addOnFailureListener {
                        println("error upload= " + it.localizedMessage)
                        _loadingState.value = false
                        _failState.value = true
                    }
                }


            }
        } else {
            userRepository.getAllUserTripData(auth.uid!!).addOnSuccessListener {
                val tripDataList = it[0].tripDataList
                for (tripData in tripDataList) {
                    viewModelScope.launch {
                        localDriverRepository.saveLocalTripData(tripData)
                    }
                }
            }.addOnFailureListener {
                println("error = " + it.localizedMessage)
                _loadingState.value = false
                _failState.value = true
            }
        }
    }

    private fun isOneHourPassed(time: Long): Boolean {
        println("one hour check function ran")
        println("current time = " + System.currentTimeMillis())
        println("other time = $time")
        val oneHourInMillis = 0.2 * 60 * 1000 //2 minutes for now    normal: //60 * 60 * 1000
        val currentTime = System.currentTimeMillis()
        println(currentTime - time)
        println("is passed " + ((currentTime - time) >= oneHourInMillis))
        return (currentTime - time) >= oneHourInMillis
    }


    fun getAllTripDataFromLocal() {
        _loadingState.value = true
        viewModelScope.launch {
            try {
                _tripDataList.value = localDriverRepository.getAllTripDataByUserId(auth.uid!!)
                _tripDataList.value?.let {
                    uploadTripDataIfNecessary()
                }
            //TODO: CHECK HERE, MAYBE THIS RUNS BEFORE ASSIGNING TRIP DATA LIST VALUES
            } catch (e: Exception) {
                userRepository.getAllUserTripData(auth.uid!!).addOnSuccessListener {
                    if (it.isNotEmpty()) {
                        val tripDataList = it[0].tripDataList
                        for (tripData in tripDataList) {
                            viewModelScope.launch {
                                localDriverRepository.saveLocalTripData(tripData)
                            }
                        }
                    }
                }.addOnFailureListener {
                    _loadingState.value = false
                    _failState.value = true
                }
            }
        }
    }

    private fun getAllTripLocationDataFromLocal(){
        _loadingState.value = true
        viewModelScope.launch {
            try{
                _tripLocationData.value = localDriverRepository.getAllTripLocationData(auth.uid!!)
                _tripLocationData.value?.let {tripLocationData->
                    uploadTripLocationData(tripLocationData)
                }
            }catch(e:Exception){
                _loadingState.value = false
                _failState.value = true
                println(e)
            }
        }
    }

    private fun uploadTripLocationData(tripLocationData: List<TripLocationData>){
        userRepository.uploadTripLocationData(tripLocationData).addOnSuccessListener {
            viewModelScope.launch {
                try{
                    localDriverRepository.deleteAllLocationList(auth.currentUser!!.uid)
                    _loadingState.value = false
                }catch(e:Exception){
                    _loadingState.value = false
                    _failState.value = true
                    println(e)
                }
            }
        }.addOnFailureListener {
            _loadingState.value = false
            _failState.value = true
        }
    }

    private fun saveDriver(localDriver: LocalDriver) {
        viewModelScope.launch {
            try {
                println("driver saved")
                localDriverRepository.saveDriver(localDriver)
                _saveDriverSuccess.value = true
                _saveDriverFailure.value = null
                _loadingState.value = false

                _triggerCampaigns.value = Unit

            } catch (error: Throwable) {
                println("error saving local data")
                _failState.value = true
                _saveDriverSuccess.value = false
                _saveDriverFailure.value = error
                _loadingState.value = false
            }
        }
    }
}








