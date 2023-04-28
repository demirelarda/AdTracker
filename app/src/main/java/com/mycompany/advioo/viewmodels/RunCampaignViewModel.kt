package com.mycompany.advioo.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycompany.advioo.models.datetime.ServerTime
import com.mycompany.advioo.models.pinfo.Nredrate
import com.mycompany.advioo.models.pinfo.Phour
import com.mycompany.advioo.models.pinfo.Pinfo
import com.mycompany.advioo.models.pinfo.PinfoResponse
import com.mycompany.advioo.repo.PinfoRepositoryInterface
import com.mycompany.advioo.repo.TimeRepositoryInterface
import com.mycompany.advioo.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RunCampaignViewModel @Inject constructor(
    private val repository : PinfoRepositoryInterface,
    private val timeRepository : TimeRepositoryInterface
) : ViewModel() {


    private val _pInfo = MutableLiveData<Resource<PinfoResponse>>()
    val pInfo : LiveData<Resource<PinfoResponse>>
        get() = _pInfo

    private val _serverTime = MutableLiveData<Resource<ServerTime>>()
    val serverTime : LiveData<Resource<ServerTime>>
        get() = _serverTime

    val pInfoList: MutableList<Pinfo> = mutableListOf() //payment info list. This will be accessible from fragment, and under obverve function, if success, then assign list elements inside this list.
    val nDownRate: MutableList<Nredrate> = mutableListOf()  //night down rate, accessible from fragment
    val pHours: MutableList<Phour> = mutableListOf() //peak hours, accessible from fragment

    private val _payment =  MutableLiveData<Double>(0.0)
    val payment : LiveData<Double>
        get() = _payment

    //FOR TESTING //TODO:REMOVE LATER
    private val _multiplier =  MutableLiveData<Double>(0.0)
    val multiplier : LiveData<Double>
        get() = _multiplier

    private val _campaignType =  MutableLiveData<String>("")
    val campaignType : LiveData<String>
        get() = _campaignType

    //


    fun getPinfoFromApi(){
        _pInfo.value = Resource.loading(null)
        viewModelScope.launch {
            val response = repository.getPinfos()
            _pInfo.value = response
        }
    }

    fun getTimeFromApi(location: String){
        _serverTime.value = Resource.loading(null)
        viewModelScope.launch {
            val response = timeRepository.getTimeFromApi(location)
            _serverTime.value = response
        }
    }

    fun calculatePayment(campaignLevel:String, distanceInKM : Double, isNight:Boolean){
        var multiplier = 0.0
        var nightEffect = 1.0
        if(isNight){
            println("isNight = "+isNight)
            for(i in nDownRate){
                println("entered night loop")
                nightEffect = i.rate
                println(i.rate)
                println("night effect = "+nightEffect)
            }
        }
        for(i in pInfoList){
            println("entered loop")
            if(i.level == campaignLevel){
                multiplier = i.multiplier
                _campaignType.value = i.level //REMOVE LATER
            }
        }
        println("multiplier = "+multiplier)
        println("night effect = "+nightEffect)
        _payment.value = _payment.value?.plus(multiplier*distanceInKM*nightEffect)
        _multiplier.value = multiplier*nightEffect

    }

    fun resetPayment() {
        _payment.postValue(0.0)
        _multiplier.postValue(0.0)
        _campaignType.postValue("")
    }





}