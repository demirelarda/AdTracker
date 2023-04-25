package com.mycompany.advioo.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mycompany.advioo.models.campaign.Campaign
import com.mycompany.advioo.repo.CampaignRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CampaignDetailsViewModel @Inject constructor(
    private val campaignRepository: CampaignRepositoryInterface
): ViewModel() {


    fun getCampaign(campaignId: String): LiveData<Campaign?> {
        return campaignRepository.getSingleCampaignById(campaignId)
    }


}