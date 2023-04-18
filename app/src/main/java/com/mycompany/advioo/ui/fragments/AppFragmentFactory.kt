package com.mycompany.advioo.ui.fragments

import android.location.LocationManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory

import com.mycompany.advioo.adapters.CityListAdapter
import com.mycompany.advioo.adapters.HomeFeedAdapter
import com.mycompany.advioo.adapters.StateListAdapter
import com.mycompany.advioo.models.user.User
import com.mycompany.advioo.ui.fragments.auth.RegisterFragment
import com.mycompany.advioo.ui.fragments.auth.city.CityListFragment
import com.mycompany.advioo.ui.fragments.auth.city.StateListFragment
import com.mycompany.advioo.ui.fragments.campaigns.HomeFragment
import javax.inject.Inject

class AppFragmentFactory @Inject constructor(
    private val cityListAdapter: CityListAdapter,
    private val stateListAdapter: StateListAdapter,
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when(className){
            HomeFragment::class.java.name -> HomeFragment()
            CityListFragment::class.java.name -> CityListFragment(cityListAdapter)
            StateListFragment::class.java.name -> StateListFragment(stateListAdapter)
            else->return super.instantiate(classLoader, className)
        }

    }

}