package com.mycompany.advioo.ui.fragments.auth.city

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mycompany.advioo.R
import com.mycompany.advioo.adapters.CityListAdapter
import javax.inject.Inject


class CityListFragment @Inject constructor(
    val cityListAdapter : CityListAdapter
) : Fragment(R.layout.fragment_city_list) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_city_list, container, false)
    }


}