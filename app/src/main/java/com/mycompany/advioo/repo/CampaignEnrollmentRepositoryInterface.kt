package com.mycompany.advioo.repo

import com.google.android.gms.tasks.Task
import com.mycompany.advioo.models.campaignapplication.CampaignApplication

interface CampaignEnrollmentRepositoryInterface {

    fun uploadCampaignApplication(campaignApplication: CampaignApplication) : Task<Void>

}