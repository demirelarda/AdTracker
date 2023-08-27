package com.mycompany.advioo.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mycompany.advioo.models.tripdata.UserTripData
import com.mycompany.advioo.repo.local.LocalDriverRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class CampaignStatsViewModel @Inject constructor(
    private val localRepo : LocalDriverRepositoryInterface,
    private val firebaseAuth: FirebaseAuth
)  : ViewModel() {



    private val _dailyTripDataList = MutableLiveData<List<UserTripData>>()
    private val _weeklyTripDataList = MutableLiveData<List<UserTripData>>()
    private val _monthlyTripDataList = MutableLiveData<List<UserTripData>>()
    private val _yearlyTripDataList = MutableLiveData<List<UserTripData>>()

    private val _earningsList = MutableLiveData<ArrayList<Triple<Int,Double,Double>>>()
    val earningsList : LiveData<ArrayList<Triple<Int,Double,Double>>>
        get() = _earningsList

    private val _dailyEarnings = MutableLiveData<Double>()
    val dailyEarnings : LiveData<Double>
        get() = _dailyEarnings

    private val _weeklyEarnings = MutableLiveData<Double>()
    val weeklyEarnings : LiveData<Double>
        get() = _weeklyEarnings

    private val _monthlyEarnings = MutableLiveData<Double>()
    val monthlyEarnings : LiveData<Double>
        get() = _monthlyEarnings

    private val _yearlyEarnings = MutableLiveData<Double>()
    val yearlyEarnings : LiveData<Double>
        get() = _yearlyEarnings

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean>
        get() = _loadingState

    private val _successState = MutableLiveData<Boolean>()
    val successState: LiveData<Boolean>
        get() = _successState

    private val _failState = MutableLiveData<Boolean>()
    val failState: LiveData<Boolean>
        get() = _failState

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage


    fun getAllPayments(campaignId: String){
        println("main calculation function ran")
        getDailyTripData(campaignId)
        getWeeklyTripData(campaignId)
        getMonthlyTripData(campaignId)
        getYearlyTripData(campaignId)
    }


    private fun getDailyTripData(campaignId: String){
        println("daily function")
        _loadingState.value = true
        viewModelScope.launch{
            try {
                _dailyTripDataList.value = localRepo.getAllTripDataFromToday(getStartAndEndOfToday().first,getStartAndEndOfToday().second,firebaseAuth.uid!!,campaignId)
                calculateEarnings(_dailyTripDataList.value!!, 0)
            }
            catch (e:Exception){
                _loadingState.value = false
                _failState.value = true
                _errorMessage.value = e.localizedMessage
            }
        }
    }



    private fun getWeeklyTripData(campaignId: String){
        _loadingState.value = true
        viewModelScope.launch{
            try {
                _weeklyTripDataList.value = localRepo.getAllTripDataFromThisWeek(getStartAndEndOfThisWeek().first,getStartAndEndOfThisWeek().second,firebaseAuth.uid!!,campaignId)
                calculateEarnings(_weeklyTripDataList.value!!, 1)
            }
            catch (e:Exception){
                _loadingState.value = false
                _failState.value = true
                _errorMessage.value = e.localizedMessage
            }
        }
    }


    private fun getMonthlyTripData(campaignId: String){
        _loadingState.value = true
        viewModelScope.launch{
            try {
                _monthlyTripDataList.value = localRepo.getAllTripDataFromThisMonth(getStartAndEndOfThisMonth().first,getStartAndEndOfThisMonth().second,firebaseAuth.uid!!,campaignId)
                calculateEarnings(_monthlyTripDataList.value!!, 2)
            }
            catch (e:Exception){
                _loadingState.value = false
                _failState.value = true
                _errorMessage.value = e.localizedMessage
            }
        }
    }

    private fun getYearlyTripData(campaignId: String){
        _loadingState.value = true
        viewModelScope.launch{
            try {
                _yearlyTripDataList.value = localRepo.getAllTripDataFromThisYear(getStartAndEndOfThisYear().first,getStartAndEndOfThisYear().second,firebaseAuth.uid!!,campaignId)
                calculateEarnings(_yearlyTripDataList.value!!, 3)
            }
            catch (e:Exception){
                _loadingState.value = false
                _failState.value = true
                _errorMessage.value = e.localizedMessage
            }
        }
    }




    private fun getStartAndEndOfToday(): Pair<Long, Long> {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val dayStart = cal.timeInMillis
        cal.add(Calendar.DAY_OF_MONTH, 1)
        val dayEnd = cal.timeInMillis - 1

        return Pair(dayStart, dayEnd)
    }

    private fun getStartAndEndOfThisWeek(): Pair<Long, Long> {
        val cal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val weekStart = cal.timeInMillis
        cal.add(Calendar.WEEK_OF_YEAR, 1)
        val weekEnd = cal.timeInMillis - 1

        return Pair(weekStart, weekEnd)
    }


    private fun getStartAndEndOfThisMonth(): Pair<Long, Long> {
        val cal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val monthStart = cal.timeInMillis
        cal.add(Calendar.MONTH, 1)
        val monthEnd = cal.timeInMillis - 1

        return Pair(monthStart, monthEnd)
    }

    private fun getStartAndEndOfThisYear(): Pair<Long, Long> {
        val cal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val yearStart = cal.timeInMillis
        cal.add(Calendar.YEAR, 1)
        val yearEnd = cal.timeInMillis - 1

        return Pair(yearStart, yearEnd)
    }

    private fun calculateEarnings(tripDataList : List<UserTripData>, timePeriod: Int){
        var totalPayment = 0.0
        var totalDistanceDriven = 0.0
        val paymentList = ArrayList<Triple<Int,Double,Double>>()
        for(tripData in tripDataList){
            totalPayment += tripData.earnedPayment
            totalDistanceDriven += tripData.kmDriven
            println("total payment = $totalPayment")
            println("total distance driven = $totalDistanceDriven")
        }
        when(timePeriod){
            0 -> paymentList.add(Triple(0,totalPayment,totalDistanceDriven))
            1 -> paymentList.add(Triple(1,totalPayment,totalDistanceDriven))
            2 -> paymentList.add(Triple(2,totalPayment,totalDistanceDriven))
            3 -> paymentList.add(Triple(3,totalPayment,totalDistanceDriven))
        }
        _earningsList.value = paymentList
    }












}