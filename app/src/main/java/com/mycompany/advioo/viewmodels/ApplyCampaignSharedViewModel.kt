package com.mycompany.advioo.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.mycompany.advioo.models.campaign.Campaign
import com.mycompany.advioo.models.campaignapplication.CampaignApplication
import com.mycompany.advioo.models.installer.Installer
import com.mycompany.advioo.repo.CampaignEnrollmentRepositoryInterface
import com.mycompany.advioo.repo.UserRepositoryInterface
import com.mycompany.advioo.repo.local.LocalDriverRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApplyCampaignSharedViewModel @Inject constructor(
    private val repo: CampaignEnrollmentRepositoryInterface,
    private val userRepo: UserRepositoryInterface,
    private val auth: FirebaseAuth,
    private val localDriverRepository: LocalDriverRepositoryInterface
) : ViewModel() {

    private val _campaignApplication = MutableLiveData<CampaignApplication>(CampaignApplication())
    val campaignApplication: LiveData<CampaignApplication>
        get() = _campaignApplication

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean>
        get() = _loadingState

    private val _failState = MutableLiveData<Boolean>()
    val failState: LiveData<Boolean>
        get() = _failState

    private val _successState = MutableLiveData<Boolean>()
    val successState: LiveData<Boolean>
        get() = _successState



    fun setSelectedCampaignLevel(campaignLevel: String) {
        val currentCampaignApplicationObject = _campaignApplication.value
        currentCampaignApplicationObject?.let {
            val updatedCampaignApplication = it.copy(selectedCampaignLevel = campaignLevel)
            _campaignApplication.value = updatedCampaignApplication
        }
    }

    fun setSelectedCampaign(campaign: Campaign) {
        val currentCampaignApplicationObject = _campaignApplication.value
        currentCampaignApplicationObject?.let {
            val updatedCampaignApplication = it.copy(selectedCampaign = campaign)
            _campaignApplication.value = updatedCampaignApplication
        }
    }

    fun setSelectedInstaller(selectedInstaller: Installer){
        val currentCampaignApplicationObject = _campaignApplication.value
        currentCampaignApplicationObject?.let {
            val updatedCampaignApplication = it.copy(selectedInstaller = selectedInstaller)
            _campaignApplication.value = updatedCampaignApplication
        }
    }

    fun setApplicantFullName(applicantFullName: String){
        val currentCampaignApplicationObject = _campaignApplication.value
        currentCampaignApplicationObject?.let {
            val updatedCampaignApplication = it.copy(applicantFullName = applicantFullName)
            _campaignApplication.value = updatedCampaignApplication
        }
    }

    fun setApplicantId(applicantId: String){
        val currentCampaignApplicationObject = _campaignApplication.value
        currentCampaignApplicationObject?.let {
            val updatedCampaignApplication = it.copy(applicantId = applicantId)
            _campaignApplication.value = updatedCampaignApplication
        }
    }

    fun setApplicationDate(applicationDate: Timestamp){
        val currentCampaignApplicationObject = _campaignApplication.value
        currentCampaignApplicationObject?.let {
            val updatedCampaignApplication = it.copy(applicationDate = applicationDate)
            _campaignApplication.value = updatedCampaignApplication
        }
    }

    fun setApplicationId(applicationId : String){
        val currentCampaignApplicationObject = _campaignApplication.value
        currentCampaignApplicationObject?.let {
            val updatedCampaignApplication = it.copy(applicationId = applicationId)
            _campaignApplication.value = updatedCampaignApplication
        }
    }


    fun uploadCampaignApplication(){
        _loadingState.value = true
        val currentCampaignApplicationObject = _campaignApplication.value
        currentCampaignApplicationObject?.let {
            val updatedCampaignApplication = it.copy(started = false, ended = false, status = 0, startDate = Timestamp.now())
            _campaignApplication.value = updatedCampaignApplication
        }
        repo.uploadCampaignApplication(_campaignApplication.value!!)
            .addOnSuccessListener {
                updateDriverStatus()
            }
            .addOnFailureListener {
                _loadingState.value = false
                _failState.value = true
            }

    }

    private fun updateDriverStatus(){
        userRepo.updateDriverCampaignStatus(auth.uid!!,
            _campaignApplication.value?.selectedCampaign!!.campaignId)
            .addOnSuccessListener {
                viewModelScope.launch {
                    val updateJob = async { localDriverRepository.updateDriverEnrolledCampaign(auth.uid!!, _campaignApplication.value!!.selectedCampaign.campaignId) }
                    updateJob.await()
                    _successState.value = true
                    _loadingState.value = false
                }
            }.addOnFailureListener {
                _failState.value = true
                _loadingState.value = false
            }
    }







}