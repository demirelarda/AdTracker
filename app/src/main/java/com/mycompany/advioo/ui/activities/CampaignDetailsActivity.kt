package com.mycompany.advioo.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.mycompany.advioo.R
import com.mycompany.advioo.ui.fragments.campaigndetails.CampaignDetailsFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CampaignDetailsActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_campaign_details)




        if(intent.hasExtra("toCampaignDetails")){
            replaceFragment(CampaignDetailsFragment())
        }



    }



    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.campaign_details_fragment_container,fragment)
        fragmentTransaction.commit()
    }
}