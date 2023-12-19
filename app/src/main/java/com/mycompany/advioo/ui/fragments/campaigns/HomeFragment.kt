package com.mycompany.advioo.ui.fragments.campaigns

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mycompany.advioo.R
import com.mycompany.advioo.adapters.HomeFeedAdapter
import com.mycompany.advioo.databinding.FragmentHomeBinding
import com.mycompany.advioo.databinding.FragmentRunCampaignBinding
import com.mycompany.advioo.models.localuser.LocalDriver
import com.mycompany.advioo.ui.activities.AppAdActivity
import com.mycompany.advioo.ui.activities.CampaignDetailsActivity
import com.mycompany.advioo.util.SnackbarHelper
import com.mycompany.advioo.viewmodels.HomeViewModel
import com.mycompany.advioo.viewmodels.MainViewModel
import com.mycompany.advioo.viewmodels.ShowPhotoViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {


    //TODO: USE USERID TO CREATE TRIPDATA DOCUMENTS ON FIRESTORE


    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel : HomeViewModel by viewModels(ownerProducer = { this } )
    private val mainViewModel : MainViewModel by activityViewModels()
    private lateinit var localDriver: LocalDriver
    @Inject
    lateinit var glide: RequestManager
    @Inject
    lateinit var homeFeedAdapter: HomeFeedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.doNecessaryProcesses()
        val binding = FragmentHomeBinding.bind(view)
        homeFeedAdapter = HomeFeedAdapter(glide)
        subscribeToObservers()
        homeFeedAdapter.setOnItemClickListener {
            val intent = Intent(requireContext(),CampaignDetailsActivity::class.java)
            println("home item clicked = ${it}")
            println("home item clicked = ${it.campaignId}")
            intent.putExtra("campaign",it)
            intent.putExtra("toCampaignDetails",true)
            intent.putExtra("enrolledCampaignId",localDriver.currentEnrolledCampaign)
            startActivity(intent)
        }
        binding.rvHomeFeed.adapter = homeFeedAdapter
        binding.rvHomeFeed.layoutManager = LinearLayoutManager(requireContext())
    }


    private fun subscribeToObservers() {
        homeViewModel.loadingState.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.homeProgressBar.visibility = View.VISIBLE
                requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavView).visibility = View.GONE
            } else {
                binding.homeProgressBar.visibility = View.GONE
                requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavView).visibility = View.VISIBLE
            }
        }

        homeViewModel.failState.observe(viewLifecycleOwner) { isFail ->
            if (isFail) {
                SnackbarHelper.showErrorSnackBar(requireView(), getString(R.string.home_feed_error))
                binding.flHomeFragment.visibility = View.GONE
                requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavView).visibility = View.VISIBLE
            }
        }

        homeViewModel.campaigns.observe(viewLifecycleOwner) { campaigns ->
            homeFeedAdapter.campaigns = campaigns
        }

        mainViewModel.userObject.observe(viewLifecycleOwner){localUser->
            if(localUser != null){
                localDriver = localUser
                homeViewModel.getCampaigns(localUser.stateId)
            }
        }
        mainViewModel.failState.observe(viewLifecycleOwner){isFail->
            if(isFail){
                SnackbarHelper.showErrorSnackBar(requireView(),getString(R.string.an_error_occurred_network))
            }
        }
        mainViewModel.loadingState.observe(viewLifecycleOwner){isLoading->
            if(isLoading){
                binding.homeProgressBar.visibility = View.VISIBLE
                requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavView).visibility = View.GONE
            }
        }
    }



}