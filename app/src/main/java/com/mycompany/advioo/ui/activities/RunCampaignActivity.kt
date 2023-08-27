package com.mycompany.advioo.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.mycompany.advioo.R
import com.mycompany.advioo.databinding.ActivityRunCampaignBinding
import com.mycompany.advioo.util.Util.ACTION_NORMAL_TRACKING_FRAGMENT
import com.mycompany.advioo.util.Util.ACTION_SHOW_CAMPAIGN_STATS
import com.mycompany.advioo.util.Util.ACTION_SHOW_TRACKING_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RunCampaignActivity : AppCompatActivity() {


    private lateinit var binding: ActivityRunCampaignBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRunCampaignBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView2) as NavHostFragment
        Navigation.setViewNavController(binding.fragmentContainerView2, navHostFragment.navController)

        navigateToRunCampaignFragmentIfNeeded(intent)

    }





    private fun navigateToRunCampaignFragmentIfNeeded(intent: Intent?){
        when (intent?.action) {
            ACTION_SHOW_TRACKING_FRAGMENT -> {
                binding.fragmentContainerView2.findNavController().navigate(R.id.action_global_runCampaignFragment)
            }
            else -> {

            }
        }
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToRunCampaignFragmentIfNeeded(intent)
    }

}