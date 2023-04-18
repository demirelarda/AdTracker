package com.mycompany.advioo.ui.activities


import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.mycompany.advioo.R
import com.mycompany.advioo.RunCampaignFragment
import com.mycompany.advioo.adapters.HomeFeedAdapter
import com.mycompany.advioo.databinding.ActivityAppAdBinding
import com.mycompany.advioo.di.AppModule
import com.mycompany.advioo.ui.fragments.AppFragmentFactory
import com.mycompany.advioo.ui.fragments.campaigns.HomeFragment
import com.mycompany.advioo.ui.fragments.campaigns.UserSettingsFragment
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
        replaceFragment(HomeFragment())
        binding.bottomNavView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> replaceFragment(HomeFragment())
                R.id.myCampaigns -> replaceFragment(RunCampaignFragment())
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




}