package com.mycompany.advioo.repo

import com.mycompany.advioo.models.campaign.Campaign
import kotlinx.coroutines.flow.Flow


interface CampaignRepositoryInterface {
    fun getCampaigns(): Flow<List<Campaign>>
}