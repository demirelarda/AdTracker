package com.mycompany.advioo.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.mycompany.advioo.models.CarImageDetails
import com.mycompany.advioo.models.campaignapplication.CampaignApplication
import com.mycompany.advioo.repo.UserRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class ShowPhotoViewModel @Inject constructor(
    private val userRepository : UserRepositoryInterface
) : ViewModel() {

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean>
        get() = _loadingState

    private val _successState = MutableLiveData<Boolean>()
    val successState: LiveData<Boolean>
        get() = _successState

    private val _failState = MutableLiveData<Boolean>()
    val failState: LiveData<Boolean>
        get() = _failState

    private val _campaignApplication = MutableLiveData<CampaignApplication>()
    val campaignApplication: LiveData<CampaignApplication>
        get() = _campaignApplication

    private val _photosToTakeList = MutableLiveData<List<String>>()
    val photosToTakeList : LiveData<List<String>>
        get() = _photosToTakeList


    fun setCampaignApplication(campaignApplication: CampaignApplication){
        _campaignApplication.value = campaignApplication
    }

    fun setPhotosToTakeList(photosToTake: List<String>){
        println("photos to take list set from fragment = ${photosToTake}")
        _photosToTakeList.postValue(photosToTake)
    }


    fun processCarImages(photoList: List<Bitmap?>) {
        _loadingState.value = true
        val compressedImages: List<ByteArray?> = photoList.map { bitmap ->
            bitmap?.let {
                val byteArrayOutputStream = ByteArrayOutputStream()
                it.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream)
                byteArrayOutputStream.toByteArray()
            }
        }

        uploadCarImages(compressedImages)
    }



    private fun uploadCarImages(byteList: List<ByteArray?>){
        val filteredList = byteList.filterNotNull()
        userRepository.uploadCarPhotos(filteredList).addOnSuccessListener {imageURLList->
            uploadCarImageDetails(_campaignApplication.value, imageURLList)
        }.addOnFailureListener {
            _loadingState.value = false
            _failState.value = true
        }
    }

    private fun uploadCarImageDetails(campaignApplication: CampaignApplication? ,imageURLList: List<String>){
        campaignApplication?.let {
            val carImageDetails = CarImageDetails(
                campaignApplication.applicantId,
                campaignApplication.applicantFullName,
                Timestamp.now(),
                campaignApplication.selectedCampaignLevel,
                campaignApplication.selectedCampaign.campaignId,
                campaignApplication.selectedInstaller.installerId,
                imageURLList
            )
            userRepository.uploadCarPhotoDetails(carImageDetails).addOnSuccessListener {
                userRepository.updateCampaignStatus(campaignApplication.applicationId,1).addOnSuccessListener {
                    _loadingState.value = false
                    _successState.value = true
                }.addOnFailureListener {
                    _loadingState.value = false
                    _failState.value = true
                }
            }.addOnFailureListener {
                _loadingState.value = false
                _failState.value = true
            }
        }
    }


}