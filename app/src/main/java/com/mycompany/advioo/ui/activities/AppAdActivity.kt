package com.mycompany.advioo.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.mycompany.advioo.R
import com.mycompany.advioo.databinding.ActivityAppAdBinding
import com.mycompany.advioo.ui.fragments.ads.HomeFragment
import com.mycompany.advioo.ui.fragments.ads.MyAdsFragment
import com.mycompany.advioo.ui.fragments.ads.UserSettingsFragment

class AppAdActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppAdBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppAdBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        replaceFragment(HomeFragment())
        binding.bottomNavView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> replaceFragment(HomeFragment())
                R.id.myCampaigns -> replaceFragment(MyAdsFragment())
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