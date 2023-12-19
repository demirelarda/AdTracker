package com.mycompany.advioo.viewmodels

import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.instacart.truetime.time.TrueTimeImpl
import com.mycompany.advioo.models.payment.PaymentRequest
import com.mycompany.advioo.models.tripdata.UserTripData
import com.mycompany.advioo.repo.CampaignRepositoryInterface
import com.mycompany.advioo.repo.UserRepositoryInterface
import com.mycompany.advioo.repo.local.LocalDriverRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import java.util.Date
import java.util.Calendar

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val repository: CampaignRepositoryInterface,
    private val localDriverRepository: LocalDriverRepositoryInterface,
    private val auth: FirebaseAuth,
    private val userRepository: UserRepositoryInterface
) : ViewModel() {

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean>
        get() = _loadingState

    private val _calculatedAmount = MutableLiveData<Pair<Double, Double>>()
    val calculatedAmount : LiveData<Pair<Double, Double>>
        get() = _calculatedAmount

    private val _shouldCalculatePayment = MutableLiveData<Boolean>()
    val shouldCalculatePayment : LiveData<Boolean>
        get() = _shouldCalculatePayment

    /*
        val trueTime = TrueTimeImpl()
        trueTime.sync()
        val currentTime = trueTime.now()
        println("current time = ${currentTime}")*/


    fun checkPaymentStatusAndCalculateIfNecessary(tripDataList: List<UserTripData>, currentCampaignMinKm: Int, startTime: Long){
        println("inside should calculate payment function")
        val trueTime = TrueTimeImpl()
        trueTime.sync()
        val currentTime = trueTime.now()
        monthPassed(currentTime,startTime)
        _loadingState.value = true
        if(tripDataList.isEmpty()){
            //if list is empty then there is no need to calculate payment
            _shouldCalculatePayment.value = false
            monthPassed(currentTime,startTime) //todo: remove
        }
        else{
            //there is data in the list
            if(monthPassed(currentTime,startTime)){
                //a month has been passed, calculate the payment
                val totalKmDriven = tripDataList.sumOf { it.kmDriven }
                if(totalKmDriven >= currentCampaignMinKm){
                    _shouldCalculatePayment.value = true
                    val totalEarnedAmount = tripDataList.sumOf { it.earnedPayment }
                    _calculatedAmount.value = Pair(totalKmDriven,totalEarnedAmount)
                }
            }
            else{
                //not passed a month
                val totalKmDriven = tripDataList.sumOf { it.kmDriven }
                val totalEarnedAmount = tripDataList.sumOf { it.earnedPayment }
                _calculatedAmount.value = Pair(totalKmDriven,totalEarnedAmount)
                _shouldCalculatePayment.value = false
                _loadingState.value = false
            }
        }
    }

    private fun monthPassed(currentTime: Date, startTime: Long) : Boolean {
        val thirtyDaysInMillis = 1000L * 60 * 60 * 24 * 30
        val difference = currentTime.time - startTime
        return if (difference >= thirtyDaysInMillis) {
            val passedDays = difference / (1000 * 60 * 60 * 24)
            val passedHours = (difference % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
            println("Passed $passedDays days and $passedHours hours")
            true
        } else {
            val remaining = thirtyDaysInMillis - difference
            val remainingDays = remaining / (1000 * 60 * 60 * 24)
            val remainingHours = (remaining % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
            println("Remaining $remainingDays days and $remainingHours hours to reach 30 days")
            false
        }
    }


    fun uploadOdometerPhotoAndPaymentRequest(byteList: List<ByteArray?>, paymentRequest: PaymentRequest){
        val filteredList = byteList.filterNotNull()
        userRepository.uploadCarPhotos(filteredList).addOnSuccessListener {imageUrlList->
            val newPaymentRequest = paymentRequest.copy(
                imageUrls = listOf()
            )
        }
    }

    fun validatePaymentForm(viewGroup: ViewGroup): Boolean {
        var isValid = true

        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            if (child is TextInputLayout) {
                val editText = child.editText
                if (editText?.text.toString().trim().isEmpty()) {
                    child.error = "${child.hint}"
                    isValid = false
                } else {
                    child.error = null
                }
            }
        }

        return isValid
    }





}