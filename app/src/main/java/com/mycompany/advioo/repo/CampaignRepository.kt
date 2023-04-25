package com.mycompany.advioo.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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



    override fun getSingleCampaignById(campaignId: String): LiveData<Campaign?> {
        val campaignLiveData = MutableLiveData<Campaign?>()
        campaignsCollection.document(campaignId).get().addOnSuccessListener { document ->
            if (document.exists()) {
                campaignLiveData.value = document.toObject(Campaign::class.java)?.copy(campaignId= document.id)
            } else {
                campaignLiveData.value = null
            }
        }.addOnFailureListener {
            campaignLiveData.value = null
        }
        return campaignLiveData
    }


    private fun QuerySnapshot.toCampaigns(): List<Campaign> {
        return documents.mapNotNull { document ->
            document.toObject(Campaign::class.java)?.apply {
                campaignId = document.id
            }
        }
    }

}