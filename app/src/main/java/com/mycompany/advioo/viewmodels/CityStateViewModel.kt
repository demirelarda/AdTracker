package com.mycompany.advioo.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycompany.advioo.models.city.CityResponse
import com.mycompany.advioo.repo.CityRepositoryInterface
import com.mycompany.advioo.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CityStateViewModel @Inject constructor(
    private val repository: CityRepositoryInterface
) : ViewModel() {

    val cities = MutableLiveData<Resource<CityResponse>>()
    val cityList : LiveData<Resource<CityResponse>>
        get() = cities

    val states = MutableLiveData<Resource<CityResponse>>()
    val stateList : LiveData<Resource<CityResponse>>
        get() = states

    private val selectedState = MutableLiveData<String>()
    val selectedUserState : LiveData<String>
        get() = selectedState


    private val selectedCity = MutableLiveData<String>()
    val selectedUserCity : LiveData<String>
        get() = selectedCity

    fun setSelectedCity(selected : String){
        selectedCity.postValue(selected)
    }



    fun setSelectedState(selected: String){
        selectedState.postValue(selected)
    }

    fun getStates(){
        states.value = Resource.loading(null)
        viewModelScope.launch {
            val response = repository.getCities()
            states.value = response
        }
    }





}