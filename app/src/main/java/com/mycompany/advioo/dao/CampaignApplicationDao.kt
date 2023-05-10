package com.mycompany.advioo.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mycompany.advioo.models.localapplication.LocalCampaignApplication

@Dao
interface CampaignApplicationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocalCampaignApplication(localCampaignApplication: LocalCampaignApplication)

    @Query("SELECT * FROM campaign_applications WHERE applicantId = :driverId")
    suspend fun getLocalCampaignApplication(driverId: String): LocalCampaignApplication?

}