package com.mycompany.advioo.repo

import com.google.android.gms.tasks.Task
import com.mycompany.advioo.models.campaignapplication.CampaignApplication


interface CampaignApplicationRepositoryInterface {

    fun getCampaignApplicationsByApplicantId(uid: String): Task<List<CampaignApplication>>

}
