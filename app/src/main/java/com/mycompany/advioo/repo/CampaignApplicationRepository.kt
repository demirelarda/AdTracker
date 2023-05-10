package com.mycompany.advioo.repo

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.mycompany.advioo.models.campaignapplication.CampaignApplication
import javax.inject.Inject

class CampaignApplicationRepository @Inject constructor(
    private val db: FirebaseFirestore
) : CampaignApplicationRepositoryInterface {

    private val campaignApplicationCollection = db.collection("campaignApplications")

    override fun getCampaignApplicationsByApplicantId(uid: String): Task<List<CampaignApplication>> {
        return campaignApplicationCollection.whereEqualTo("applicantId", uid).get()
            .continueWith { task ->
                if (task.isSuccessful) {
                    task.result?.documents?.mapNotNull { it.toObject(CampaignApplication::class.java) }
                } else {
                    null
                }
            }
    }
}
