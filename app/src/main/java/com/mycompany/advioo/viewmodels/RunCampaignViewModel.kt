package com.mycompany.advioo.viewmodels


import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mycompany.advioo.util.HaversineCalculateDistance
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RunCampaignViewModel @Inject constructor(
    val calculateDistanceWithHaversine: HaversineCalculateDistance,
) : ViewModel() {

    private var previousLocation: Location? = null
    private var totalDistance = 0f


    private val _distanceDriven = MutableLiveData<Double>(0.0)
    val distanceDriven: LiveData<Double> = _distanceDriven

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double) {
        val distance = calculateDistanceWithHaversine.calculateDistance(lat1, lon1, lat2, lon2)
        val currentDistance = _distanceDriven.value ?: 0.0
        _distanceDriven.value = currentDistance + distance
        println("total = ${_distanceDriven.value}")
    }

    fun resetDistance() {
        _distanceDriven.value = 0.0
    }

    fun updateDistance(location: Location): Float {
        val newDistance = previousLocation?.distanceTo(location) ?: 0f
        totalDistance += newDistance
        previousLocation = location
        return totalDistance
    }

}