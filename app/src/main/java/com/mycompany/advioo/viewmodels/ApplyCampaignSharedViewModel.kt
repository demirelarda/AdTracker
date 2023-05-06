package com.mycompany.advioo.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.mycompany.advioo.models.campaign.Campaign
import com.mycompany.advioo.models.campaignapplication.CampaignApplication
import com.mycompany.advioo.models.installer.Installer
import com.mycompany.advioo.models.user.Driver

class ApplyCampaignSharedViewModel : ViewModel() {

    private val _campaignApplication = MutableLiveData<CampaignApplication>(CampaignApplication())
    val campaignApplication: LiveData<CampaignApplication>
        get() = _campaignApplication




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
            val updatedCampaignApplication = it.copy(applicationId = applicantId)
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


    fun enrollCampaign(){

    }








}