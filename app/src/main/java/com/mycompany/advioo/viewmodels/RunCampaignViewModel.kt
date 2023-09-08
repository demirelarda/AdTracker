package com.mycompany.advioo.viewmodels


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycompany.advioo.models.campaign.LatLngPoint
import com.mycompany.advioo.models.datetime.ServerTime
import com.mycompany.advioo.models.pinfo.Nredrate
import com.mycompany.advioo.models.pinfo.Phour
import com.mycompany.advioo.models.pinfo.Pinfo
import com.mycompany.advioo.models.pinfo.PinfoResponse
import com.mycompany.advioo.models.tripdata.TripLocationData
import com.mycompany.advioo.models.tripdata.UserTripData
import com.mycompany.advioo.repo.PinfoRepositoryInterface
import com.mycompany.advioo.repo.local.LocalDriverRepositoryInterface
import com.mycompany.advioo.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RunCampaignViewModel @Inject constructor(
    private val repository : PinfoRepositoryInterface,
    private val localUserRepository: LocalDriverRepositoryInterface
) : ViewModel() {




    val currentSpeed = MutableLiveData<Float>()

    private val _pInfo = MutableLiveData<Resource<PinfoResponse?>>()
    val pInfo : LiveData<Resource<PinfoResponse?>>
        get() = _pInfo

    private val _serverTime = MutableLiveData<Resource<ServerTime?>>()
    val serverTime : LiveData<Resource<ServerTime?>>
        get() = _serverTime


    val pInfoList: MutableList<Pinfo> = mutableListOf() //payment info list. This will be accessible from fragment, and under obverve function, if success, then assign list elements inside this list.
    val nDownRate: MutableList<Nredrate> = mutableListOf()  //night down rate, accessible from fragment
    val pHours: MutableList<Phour> = mutableListOf() //peak hours, accessible from fragment

    private val _payment =  MutableLiveData<Double?>(0.0)
    val payment : LiveData<Double?>
        get() = _payment

    //FOR TESTING //TODO:REMOVE LATER
    private val _multiplier =  MutableLiveData<Double?>(0.0)
    val multiplier : LiveData<Double?>
        get() = _multiplier

    private val _campaignType =  MutableLiveData<String?>("")
    val campaignType : LiveData<String?>
        get() = _campaignType

    private val _campaignLastUpdateTime = MutableLiveData<Long?>()
    val campaignLastUpdateTime : LiveData<Long?>
        get() = _campaignLastUpdateTime

    private val _sessionID = MutableLiveData<String?>()
    val sessionID: LiveData<String?>
        get() = _sessionID

    private val _tripLocationSessionID = MutableLiveData<String?>()
    val tripLocationSessionID : LiveData<String?>
        get() = _tripLocationSessionID

    private val _tripPreviousLocationList = MutableLiveData<List<LatLngPoint>?>()
    val tripPreviousLocationList : LiveData<List<LatLngPoint>?>
        get() = _tripPreviousLocationList

    fun resetPreviousLocationDataList(){
        _tripPreviousLocationList.value = null
    }

    init {
        generateNewSessionID()
        Log.d("Debug", "RunCampaignViewModel initialized")
        println("init called, new session id generated")
    }

    val allDataReady = MediatorLiveData<Boolean>().apply {
        addSource(_pInfo) { checkAllDataReady() }
        addSource(_sessionID) { checkAllDataReady() }
        addSource(_tripLocationSessionID) { checkAllDataReady() }
        addSource(_tripPreviousLocationList) { checkAllDataReady() }
    }

    private fun checkAllDataReady() {
        println("entered check all data ready")
        val isReady = _pInfo.value != null &&
                _sessionID.value != null &&
                _tripLocationSessionID.value != null &&
                pInfoList.isNotEmpty() &&
                nDownRate.isNotEmpty() &&
                pHours.isNotEmpty() &&
                _tripPreviousLocationList.value != null

        allDataReady.value = isReady
    }



    fun saveLocalTripData(localTripData: UserTripData){
        viewModelScope.launch {
            try{
                localUserRepository.saveLocalTripData(localTripData)
            }
            catch (e:Exception){
                println(e.localizedMessage) //TODO: SET ERROR STATE AND SHOW SNACKBAR IN UI (NO SPACE AVAILABLE IN STORAGE ERROR)
            }

        }
    }

    fun saveTripLocationData(locationData: TripLocationData){
        viewModelScope.launch {
            try{
                localUserRepository.saveTripLocationData(locationData) //TODO: SET ERROR STATE AND SHOW SNACKBAR IN UI (NO SPACE AVAILABLE IN STORAGE ERROR)
            }
            catch(e:Exception){
                println(e.localizedMessage)
            }
        }
    }

    fun checkTripLocationDataFromLastHour(driverId: String){
        viewModelScope.launch {
            try {
                val tripLocationDataList = localUserRepository.getTripLocationDataFromLastHour(driverId,System.currentTimeMillis())
                val lastElement = tripLocationDataList.lastOrNull()
                if(lastElement != null){
                    if(lastElement.locationList.size < 8000){
                        _tripLocationSessionID.value = lastElement.tripId
                        _tripPreviousLocationList.value = lastElement.locationList
                    }
                    else{
                        _tripLocationSessionID.value = UUID.randomUUID().toString() + "_" + System.currentTimeMillis()
                        _tripPreviousLocationList.value = listOf()
                    }
                }
                else{
                    _tripLocationSessionID.value = UUID.randomUUID().toString() + "_" + System.currentTimeMillis()
                    _tripPreviousLocationList.value = listOf()
                }
            }
            catch (e:Exception){
                _tripLocationSessionID.value = UUID.randomUUID().toString() + "_" + System.currentTimeMillis()
                _tripPreviousLocationList.value = listOf()
                println(e)
            }
        }
    }

    private fun generateNewSessionID(){
        _payment.postValue(0.0)
        _sessionID.value = UUID.randomUUID().toString() + "_" + System.currentTimeMillis()
    }



    fun getPinfoFromApi(){
        _pInfo.value = Resource.loading(null)
        viewModelScope.launch {
            val response = repository.getPinfos()
            _pInfo.value = response
        }
    }


    fun calculatePayment(campaignLevel:String, distanceInKM : Double, isNight:Boolean){
        var multiplier = 0.0
        var nightEffect = 1.0


        if(isNight){
            println("isNight = $isNight")
            for(i in nDownRate){
                println("entered night loop")
                nightEffect = i.rate
                println(i.rate)
                println("night effect = $nightEffect")
            }
        }
        for(i in pInfoList){
            println("entered loop")
            if(i.level == campaignLevel){
                multiplier = i.multiplier
                _campaignType.value = i.level //REMOVE LATER
            }
        }
        println("multiplier = $multiplier")
        println("night effect = $nightEffect")
        _payment.value = _payment.value?.plus(multiplier*distanceInKM*nightEffect)
        _multiplier.value = multiplier*nightEffect

    }

    fun resetPayment() {
        allDataReady.postValue(false)
        _payment.postValue(0.0)
        _multiplier.postValue(0.0)
        _campaignType.postValue("")
        _tripLocationSessionID.postValue(null)
        _sessionID.postValue(null)
        _tripLocationSessionID.postValue(null)
        _tripPreviousLocationList.postValue(null)
    }

    override fun onCleared() {
        super.onCleared()
        println("clerad")
    }





}