package com.mycompany.advioo.models.campaignapplication

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.mycompany.advioo.models.campaign.Campaign
import com.mycompany.advioo.models.installer.Installer
import com.mycompany.advioo.models.user.Driver
import kotlinx.parcelize.Parcelize

@Parcelize
data class CampaignApplication(

    val applicationId : String = "",
    //val applicant: Driver,
    val applicantId : String = "",
    val applicantFullName: String = "",
    val applicationDate: Timestamp = Timestamp.now(),
    val selectedCampaign: Campaign = Campaign(),
    val selectedCampaignLevel: String = "",
    var selectedInstaller: Installer = Installer(),

    ) : Parcelable