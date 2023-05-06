package com.mycompany.advioo.repo

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mycompany.advioo.models.campaignapplication.CampaignApplication
import javax.inject.Inject

class CampaignEnrollmentRepository @Inject constructor(
    private val db: FirebaseFirestore
) : CampaignEnrollmentRepositoryInterface {

    private val campaignApplicationCollection = db.collection("campaignApplications")
    override fun uploadCampaignApplication(campaignApplication: CampaignApplication): Task<Void> {
        return campaignApplicationCollection
            .document(campaignApplication.applicationId)
            .set(campaignApplication, SetOptions.merge())
    }


}