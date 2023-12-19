package com.mycompany.advioo.repo

import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.mycompany.advioo.models.campaign.Campaign


interface CampaignRepositoryInterface {
    fun getCampaigns(stateId: String): Task<List<Campaign>>
    fun getSingleCampaignById(campaignId: String): Task<List<Campaign>>
}
