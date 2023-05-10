package com.mycompany.advioo.repo.local

import com.mycompany.advioo.models.localapplication.LocalCampaignApplication
import com.mycompany.advioo.models.localuser.LocalDriver

interface LocalDriverRepositoryInterface {

    suspend fun saveDriver(driver: LocalDriver)

    suspend fun getDriver(driverId: String): LocalDriver?

    suspend fun saveCampaignApplication(campaignApplication: LocalCampaignApplication)

    suspend fun getCampaignApplication(driverId: String): LocalCampaignApplication?

}