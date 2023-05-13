package com.mycompany.advioo.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mycompany.advioo.models.campaign.Campaign
import com.mycompany.advioo.models.localapplication.LocalCampaignApplication
import com.mycompany.advioo.repo.CampaignApplicationRepositoryInterface
import com.mycompany.advioo.repo.local.LocalDriverRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyCampaignsViewModel @Inject constructor(
    private val campaignApplicationRepository: CampaignApplicationRepositoryInterface,
    private val localDriverRepository: LocalDriverRepositoryInterface,
    private val auth: FirebaseAuth,
) : ViewModel() {

    private val _loadingState = MutableLiveData<Boolean>()
    val loadingState: LiveData<Boolean>
        get() = _loadingState

    private val _failState = MutableLiveData<Boolean>()
    val failState: LiveData<Boolean>
        get() = _failState

    private val _campaignApplication = MutableLiveData<LocalCampaignApplication>()
    val campaignApplication : LiveData<LocalCampaignApplication>
        get() = _campaignApplication

    val _campaignApplicationIsEmpty = MutableLiveData<Boolean>()


    private val _saveCampaignApplicationSuccess = MutableLiveData<Boolean>()
    val saveCampaignApplicationSuccess: LiveData<Boolean>
        get() = _saveCampaignApplicationSuccess

    private val _saveCampaignApplicationFailure = MutableLiveData<Throwable?>()
    val saveCampaignApplicationFailure: LiveData<Throwable?>
        get() = _saveCampaignApplicationFailure

    private val _triggerCampaignApplication = MutableLiveData<Unit>()




    fun getCampaignApplication(){
        viewModelScope.launch {
            _loadingState.value = true
            //check if campaign id is available on local
            val campaignApplicationId = localDriverRepository.getCampaignApplication(auth.uid!!)?.applicationId
            if(campaignApplicationId==null){
                getActiveUserCampaignFromFirestore()
            }
            else{
                getCampaignApplicationFromLocal()
            }
            _loadingState.value = false
        }
    }

    val liveCampaignApplication: LiveData<List<Campaign>> = _triggerCampaignApplication.switchMap {
        liveData {
            _loadingState.value = true
            //check if campaign id is available on local
            val campaignApplicationId = localDriverRepository.getCampaignApplication(auth.uid!!)?.applicationId
            if(campaignApplicationId==null){
                getActiveUserCampaignFromFirestore()
            }
            else{
                getCampaignApplicationFromLocal()
            }
            _loadingState.value = false
        }
    }

    private fun getActiveUserCampaignFromFirestore(){
        println("call from firestore to get campaign application object ")
        _loadingState.value = true
        campaignApplicationRepository.getCampaignApplicationsByApplicantId(auth.uid!!).addOnSuccessListener {campaignApplication->
            if(campaignApplication.isNotEmpty()){
                _campaignApplicationIsEmpty.value = false
                val localCampaignApplication = LocalCampaignApplication(
                    applicationId = campaignApplication[0].applicationId,
                    applicantId = campaignApplication[0].applicantId,
                    applicantFullName = campaignApplication[0].applicantFullName,
                    applicationDate = campaignApplication[0].applicationDate.toDate().time,
                    selectedInstaller = campaignApplication[0].selectedInstaller,
                    selectedCampaign = campaignApplication[0].selectedCampaign,
                    selectedCampaignLevel = campaignApplication[0].selectedCampaignLevel
                )
                saveCampaignApplication(localCampaignApplication)
                _campaignApplication.value = localCampaignApplication
            }
            else{
                _campaignApplicationIsEmpty.value = true
            }
        }

    }


    private fun getCampaignApplicationFromLocal(){
        viewModelScope.launch {
            try {
                val localCampaignApplication = localDriverRepository.getCampaignApplication(auth.uid!!)
                if(localCampaignApplication != null){
                    _campaignApplicationIsEmpty.value = false
                    _campaignApplication.value = localCampaignApplication!!
                }
                else{
                    _campaignApplicationIsEmpty.value = true
                }
                println("local application")
            }
            catch (error:Throwable){
                println(error.message)
            }
        }
    }



    private fun saveCampaignApplication(localCampaignApplication: LocalCampaignApplication){
        viewModelScope.launch {
            try {
                _loadingState.value = false
                localDriverRepository.saveCampaignApplication(localCampaignApplication)
                _saveCampaignApplicationSuccess.value = true
                _saveCampaignApplicationFailure.value = null
                _loadingState.value = false

            } catch (error: Throwable) {
                _saveCampaignApplicationSuccess.value = false
                _saveCampaignApplicationFailure.value = error
                _loadingState.value = false
            }
        }
    }

}