package com.mycompany.advioo.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mycompany.advioo.models.campaign.Campaign
import com.mycompany.advioo.models.localuser.LocalDriver
import com.mycompany.advioo.models.tripdata.TotalTripData
import com.mycompany.advioo.models.tripdata.TripLocationData
import com.mycompany.advioo.models.tripdata.UserTripData
import com.mycompany.advioo.repo.CampaignRepositoryInterface
import com.mycompany.advioo.repo.UserRepositoryInterface
import com.mycompany.advioo.repo.local.LocalDriverRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlin.math.min

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: CampaignRepositoryInterface,
    private val localDriverRepository: LocalDriverRepositoryInterface,
    private val auth: FirebaseAuth,
    private val userRepository: UserRepositoryInterface
) : ViewModel() {


    //TODO: YAPILACAKLAR:
    // BURADAKİ İŞLEMLERİ AppAdActivity'e bağlı şekilde yap.
    // BURADA YAPILAN İŞLEMLERİ HOMEFRAGMENT'TA DA OBSERVE ET.
    // HOME VIEW MODEL'DA getCampaigns() FONKSİONU VAR. BU FONKSİYON STATEID İSTİYOR.
    // O YÜZDEN _userObject'i home fragment'ta observe et. null olmazsa getCampaigns()'i çağır.
    // Şu şekilde olabilir:  (_userObject'i observe ediyoruz.) getCampaigns(userObject.value.stateId)
    // BU ŞEKİLDE DAHA DOĞRU Bİ YÖNETİM YAPMIŞ OLURUZ.
    // DAHA SONRA PAYMENT İŞLERİNE GEÇ. MAİN VİEW MODEL'DAKİ DATALARA HER YERDEN ULAŞILABİLECEĞİNİ UNUTMA.
    // GERKETİĞİNDE SIFIRLAMA DA YAP.
    // SWIPE REFRESH EKLE.

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean>
        get() = _loadingState

    private val _successState = MutableLiveData<Boolean>()
    val successState: LiveData<Boolean>
        get() = _successState

    private val _failState = MutableLiveData<Boolean>()
    val failState: LiveData<Boolean>
        get() = _failState

    private val _userObject = MutableLiveData<LocalDriver?>()
    val userObject: LiveData<LocalDriver?>
        get() = _userObject


    private val _tripDataList = MutableLiveData<List<UserTripData>>()
    val tripDataList : LiveData<List<UserTripData>>
        get() = _tripDataList

    private val _tripLocationData = MutableLiveData<List<TripLocationData>>()

    private val _currentCampaign = MutableLiveData<Campaign>()
    val currentCampaign : LiveData<Campaign>
        get() = _currentCampaign



    fun doNecessaryProcesses(){
        if(auth.currentUser != null){
            viewModelScope.launch {
                val localUser = localDriverRepository.getDriver(auth.uid!!)
                if(localUser != null){
                    _userObject.value = localUser
                    getAllTripDataFromLocalAndUpload()
                }
                else{
                    getUserFromFirestore()
                }
            }
        }
        else{
            _failState.value = true
        }
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
                    carModel = driver.carModel,
                    carCondition = driver.carCondition,
                    carYear = driver.carYear,
                    avgKmDriven = driver.avgKmDriven,
                    rideShareDriver = driver.rideShareDriver,
                    allowedContact = driver.allowedContact,
                    zipCode = driver.zipCode,
                    carBrand = driver.carBrand,
                    currentCampaignApplicationId = driver.currentCampaignApplicationId,
                    phoneNumber = driver.driverPhoneNumber

                )
                _userObject.value = localDriver
                saveDriver(localDriver)
                getAllTripDataFromLocalAndUpload()

            }
            .addOnFailureListener {
                println("error getting user data = " + it.localizedMessage)
                _loadingState.value = false
                _failState.value = true
            }
    }

    private fun getAllTripDataFromLocalAndUpload() {
        _loadingState.value = true
        viewModelScope.launch {
            try {
                _tripDataList.value = localDriverRepository.getAllTripDataByUserId(auth.uid!!)
                _tripDataList.value?.let {
                    uploadTripDataIfNecessary()
                }
                //TODO: CHECK HERE, MAYBE THIS RUNS BEFORE ASSIGNING TRIP DATA LIST VALUES
            } catch (e: Exception) {
                val localDriver = localDriverRepository.getDriver(auth.currentUser!!.uid)
                userRepository.getAllUserTripData(auth.uid!!,localDriver!!.currentCampaignApplicationId).addOnSuccessListener {
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
                    tripDataList = tripDataList,
                    campaignApplicationId = tripDataList[0].campaignApplicationId
                )
                userRepository.uploadTripData(totalTripData).addOnSuccessListener {
                    println("uploaded trip data successfuly")
                    userRepository.getAllUserTripData(auth.uid!!,tripDataList[0].campaignApplicationId).addOnSuccessListener {
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
            viewModelScope.launch {
                val localDriver = localDriverRepository.getDriver(auth.currentUser!!.uid)
                userRepository.getAllUserTripData(auth.uid!!,localDriver!!.currentEnrolledCampaign).addOnSuccessListener {
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

    private fun getAllTripLocationDataFromLocal(){
        _loadingState.value = true
        viewModelScope.launch {
            try{
                _tripLocationData.value = localDriverRepository.getAllTripLocationData(auth.uid!!)
                _tripLocationData.value?.let { tripLocationDataList ->
                    if (tripLocationDataList.isNotEmpty()) {
                        val lastElement = tripLocationDataList.last()
                        val newDriverId = lastElement.driverId
                        val newCampaignId = lastElement.campaignId
                        val newDate = lastElement.date

                        val allLocations = tripLocationDataList.flatMap { it.locationList }

                        val size = allLocations.size
                        var startIndex = 0
                        var endIndex = min(8000, size)
                        var remainingLocations = size

                        val newTripLocationDataList = mutableListOf<TripLocationData>()

                        while (remainingLocations > 0) {
                            val newTripId = if (remainingLocations == size) lastElement.tripId else UUID.randomUUID().toString() + "_" + System.currentTimeMillis()
                            val sublist = allLocations.subList(startIndex, endIndex)

                            val newTripLocationData = TripLocationData(
                                tripId = newTripId,
                                locationList = sublist,
                                driverId = newDriverId,
                                campaignId = newCampaignId,
                                date = newDate
                            )

                            newTripLocationDataList.add(newTripLocationData)

                            remainingLocations -= 8000
                            startIndex = endIndex
                            endIndex += min(8000, remainingLocations)
                        }

                        uploadTripLocationData(newTripLocationDataList)

                    }
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
                localDriverRepository.updateCurrentCampaignApplicationId(localDriver.id,localDriver.currentCampaignApplicationId)
                localDriverRepository.updateDriverEnrolledCampaign(localDriver.id,localDriver.currentEnrolledCampaign)
                _successState.value = true
                _loadingState.value = false

            } catch (error: Throwable) {
                println("error saving local data")
                _failState.value = true
                _loadingState.value = false
            }
        }
    }

    private fun getCurrentCampaign(campaignId: String){
        _loadingState.value = true
        repository.getSingleCampaignById(campaignId)
            .addOnSuccessListener {
                _currentCampaign.value = it[0]
                _loadingState.value = false
            }
            .addOnFailureListener {
                _loadingState.value = false
                _failState.value = true
            }
    }




}