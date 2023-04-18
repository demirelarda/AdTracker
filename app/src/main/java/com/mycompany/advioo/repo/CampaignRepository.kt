package com.mycompany.advioo.repo

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.mycompany.advioo.models.campaign.Campaign
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class CampaignRepository @Inject constructor(
    private val db: FirebaseFirestore
) : CampaignRepositoryInterface{

    private val campaignsCollection = db.collection("campaigns")

    @ExperimentalCoroutinesApi
    override fun getCampaigns(): Flow<List<Campaign>> = callbackFlow {
        val listenerRegistration = campaignsCollection.addSnapshotListener { value, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val campaigns = value?.toCampaigns() ?: emptyList()
            trySend(campaigns).isSuccess
        }

        awaitClose { listenerRegistration.remove() }
    }

    private fun QuerySnapshot.toCampaigns(): List<Campaign> {
        return documents.mapNotNull { document ->
            document.toObject(Campaign::class.java)?.apply {
                campaignId = document.id
            }
        }
    }

}