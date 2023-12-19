package com.mycompany.advioo.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.mycompany.advioo.models.campaign.Campaign
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CampaignRepository @Inject constructor(
    private val db: FirebaseFirestore
) : CampaignRepositoryInterface {

    private val campaignsCollection = db.collection("campaigns")

    override fun getCampaigns(stateId: String): Task<List<Campaign>> {
        return campaignsCollection
            .whereEqualTo("campaignStateId", stateId)
            .get()
            .continueWith { task ->
                val documents = task.result?.documents ?: emptyList()
                documents.mapNotNull { document ->
                    document.toObject(Campaign::class.java)?.apply {
                        campaignId = document.id
                    }
                }
            }
    }


    override fun getSingleCampaignById(campaignId: String): Task<List<Campaign>> {
        return campaignsCollection
            .whereEqualTo("campaignId", campaignId)
            .get()
            .continueWith { task ->
                val documents = task.result?.documents ?: emptyList()
                documents.mapNotNull { document ->
                    document.toObject(Campaign::class.java)?.apply {
                        this.campaignId = document.id
                    }
                }
            }
    }

    private fun QuerySnapshot.toCampaigns(): List<Campaign> {
        return documents.mapNotNull { document ->
            document.toObject(Campaign::class.java)?.apply {
                campaignId = document.id
            }
        }
    }


}