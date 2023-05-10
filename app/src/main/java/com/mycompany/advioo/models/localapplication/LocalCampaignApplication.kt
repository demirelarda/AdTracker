package com.mycompany.advioo.models.localapplication

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mycompany.advioo.models.campaign.Campaign
import com.mycompany.advioo.models.installer.Installer

@Entity(tableName = "campaign_applications")
data class LocalCampaignApplication(
    @PrimaryKey val applicationId : String,
    //val applicant: Driver,
    val applicantId : String,
    val applicantFullName: String,
    val applicationDate: Long,
    val selectedCampaign: Campaign,
    val selectedCampaignLevel: String,
    var selectedInstaller: Installer,
)