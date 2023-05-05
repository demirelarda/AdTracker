package com.mycompany.advioo.repo

import androidx.lifecycle.LiveData
import com.mycompany.advioo.models.campaign.Campaign


interface CampaignRepositoryInterface {
    fun getCampaigns(stateId: String): LiveData<List<Campaign>>

    fun getSingleCampaignById(campaignId: String): LiveData<Campaign?>
}
