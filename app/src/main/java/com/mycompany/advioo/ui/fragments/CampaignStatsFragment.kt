package com.mycompany.advioo.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mycompany.advioo.R
import com.mycompany.advioo.adapters.CampaignStatsAdapter
import com.mycompany.advioo.databinding.FragmentCampaignStatsBinding
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CampaignStatsFragment : Fragment() {


    private var _binding: FragmentCampaignStatsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCampaignStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavView).visibility = View.GONE
        val dailyIncome = Triple("Daily","30.02","204.2 KM")
        val weeklyIncome = Triple("Weekly","120.8","508.9 KM")
        val monthlyIncome = Triple("Monthly","200.1","500.1 KM")
        val yearlyIncome = Triple("Yearly","1400000.32","8900.2 KM")
        val statsList = ArrayList<Triple<String,String,String>>()
        statsList.add(dailyIncome)
        statsList.add(weeklyIncome)
        statsList.add(monthlyIncome)
        statsList.add(yearlyIncome)
        val dotsIndicator = requireActivity().findViewById<SpringDotsIndicator>(R.id.spring_dots_indicator)
        val adapter = CampaignStatsAdapter(statsList)
        binding.viewPagerCampaignStats.adapter = adapter
        dotsIndicator.attachTo(binding.viewPagerCampaignStats)



    }







}