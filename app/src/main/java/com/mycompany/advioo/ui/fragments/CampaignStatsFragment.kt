package com.mycompany.advioo.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mycompany.advioo.R
import com.mycompany.advioo.adapters.CampaignStatsAdapter
import com.mycompany.advioo.databinding.FragmentCampaignStatsBinding
import com.mycompany.advioo.viewmodels.CampaignStatsViewModel
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CampaignStatsFragment : Fragment() {

    private var _binding: FragmentCampaignStatsBinding? = null
    private val binding get() = _binding!!
    private val campaignStatsViewModel: CampaignStatsViewModel by viewModels()
    val statsList = ArrayList<Triple<String,String,String>>()
    var dailyIncome = Triple("","","")
    var weeklyIncome = Triple("","","")
    var monthlyIncome = Triple("","","")
    var yearlyIncome = Triple("","","")
    private var campaignId: String? = null
    @Inject
    lateinit var campaignStatsAdapter: CampaignStatsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCampaignStatsBinding.inflate(inflater, container, false)
        campaignId = arguments?.getString("campaignId")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavView).visibility = View.GONE
        campaignStatsViewModel.getAllPayments(campaignId!!)
        val dotsIndicator = requireActivity().findViewById<SpringDotsIndicator>(R.id.spring_dots_indicator)
        campaignStatsAdapter = CampaignStatsAdapter()
        binding.viewPagerCampaignStats.adapter = campaignStatsAdapter
        dotsIndicator.attachTo(binding.viewPagerCampaignStats)

        binding.ivBtnBackFromCampaignStats.setOnClickListener {
            requireActivity().onBackPressed()
        }

    }


    private fun subscribeToObservers(){
        println("subcscibed to observers")
        campaignStatsViewModel.earningsList.observe(viewLifecycleOwner){paymentsList->
            println("payment list = $paymentsList")
            println("statsList = $statsList")
            for(payment in paymentsList){
                when(payment.first){
                    0-> {
                        dailyIncome = Triple(getString(R.string.today),String.format("%.2f",payment.second),String.format("%.2f",payment.third)+" "+getString(R.string.km))
                        statsList.add(dailyIncome)
                        println("daily income = $dailyIncome")
                    }
                    1-> {
                        weeklyIncome = Triple(getString(R.string.this_week),String.format("%.2f",payment.second),String.format("%.2f",payment.third)+" "+getString(R.string.km))
                        statsList.add(weeklyIncome)
                    }
                    2-> {
                        monthlyIncome = Triple(getString(R.string.this_month),String.format("%.2f",payment.second),String.format("%.2f",payment.third)+" "+getString(R.string.km))
                        statsList.add(monthlyIncome)
                    }
                    3-> {
                        yearlyIncome = Triple(getString(R.string.this_year),String.format("%.2f",payment.second),String.format("%.2f",payment.third)+" "+getString(R.string.km))
                        statsList.add(yearlyIncome)

                    }
                }
            }
            println("stats list after observed = $statsList")
            val sortOrder = listOf(getString(R.string.today),getString(R.string.this_week),getString(R.string.this_month),getString(R.string.this_year))
            val comparator = compareBy<Triple<String, String, String>> { sortOrder.indexOf(it.first) }
            statsList.sortWith(comparator)

            campaignStatsAdapter.campaignHistoryList = statsList


        }

    }







}