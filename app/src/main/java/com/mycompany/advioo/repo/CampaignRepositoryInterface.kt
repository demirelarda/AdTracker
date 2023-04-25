package com.mycompany.advioo.repo

import androidx.lifecycle.LiveData
import com.mycompany.advioo.models.campaign.Campaign
import kotlinx.coroutines.flow.Flow


interface CampaignRepositoryInterface {
    fun getCampaigns(): Flow<List<Campaign>>

    fun getSingleCampaignById(campaignId: String): LiveData<Campaign?>
}