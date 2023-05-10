package com.mycompany.advioo.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.mycompany.advioo.repo.CampaignApplicationRepositoryInterface
import javax.inject.Inject

class MyCampaignsViewModel @Inject constructor(
    private val campaignApplicationRepository: CampaignApplicationRepositoryInterface,
    private val auth: FirebaseAuth,
) : ViewModel() {

    private fun getActiveUserCampaignFromFirestore(){
        campaignApplicationRepository.getCampaignApplication(auth.uid!!)
    }

    private fun getActiveUserCampaignFromLocal(){

    }

}