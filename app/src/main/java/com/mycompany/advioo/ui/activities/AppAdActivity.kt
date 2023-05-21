package com.mycompany.advioo.ui.activities


import android.content.Intent
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.mycompany.advioo.ui.fragments.campaigns.MyCampaignsFragment
import com.mycompany.advioo.R
import com.mycompany.advioo.databinding.ActivityAppAdBinding
import com.mycompany.advioo.di.AppModule
import com.mycompany.advioo.ui.fragments.AppFragmentFactory
import com.mycompany.advioo.ui.fragments.campaigns.HomeFragment
import com.mycompany.advioo.ui.fragments.campaigns.UserSettingsFragment
import com.mycompany.advioo.RunCampaignFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AppAdActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppAdBinding
    @Inject
    lateinit var fragmentFactory: AppFragmentFactory
    @Inject
    lateinit var locationManager: LocationManager




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.fragmentFactory = fragmentFactory
        binding = ActivityAppAdBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        locationManager = AppModule.provideLocationManager(this)

        intentHandler(intent)



        binding.bottomNavView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> replaceFragment(HomeFragment())
                R.id.myCampaigns -> replaceFragment(MyCampaignsFragment())
                R.id.account -> replaceFragment(UserSettingsFragment())
                else ->{

                }
            }
            true
        }

    }


    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.mainFrameLayout,fragment)
        fragmentTransaction.commit()
    }

    private fun intentHandler(intent: Intent) {
        when {
            intent.getBooleanExtra("toRunCampaign", false) -> {
                if(intent.getBooleanExtra("toRunCampaign",false)){
                    //replaceFragment(RunCampaignFragment())
                }
                else{
                    replaceFragment(HomeFragment())
                    binding.bottomNavView.selectedItemId = R.id.home
                }
            }
            intent.getBooleanExtra("toMyCampaigns", false) -> {
                if(intent.getBooleanExtra("toMyCampaigns",false)){
                    replaceFragment(MyCampaignsFragment())
                    binding.bottomNavView.selectedItemId = R.id.myCampaigns
                }
                else{
                    replaceFragment(HomeFragment())
                    binding.bottomNavView.selectedItemId = R.id.home
                }
            }
            intent.getBooleanExtra("toAccountSettings", false) -> {
                if(intent.getBooleanExtra("toAccountSettings",false)){
                    replaceFragment(UserSettingsFragment())
                    binding.bottomNavView.selectedItemId = R.id.account
                }
                else{
                    replaceFragment(HomeFragment())
                    binding.bottomNavView.selectedItemId = R.id.home
                }
            }
            else -> {
                replaceFragment(HomeFragment())
                binding.bottomNavView.selectedItemId = R.id.home
            }
        }
    }





}