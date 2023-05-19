package com.mycompany.advioo.ui.fragments.campaigndetails

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.mycompany.advioo.R
import com.mycompany.advioo.databinding.FragmentApplyCampaignSucessBinding
import com.mycompany.advioo.models.campaignapplication.CampaignApplication
import com.mycompany.advioo.ui.activities.AppAdActivity
import com.mycompany.advioo.ui.activities.CampaignDetailsActivity
import com.mycompany.advioo.ui.fragments.auth.RegisterAddressDetailsFragmentArgs
import com.mycompany.advioo.util.SnackbarHelper

class ApplyCampaignSuccessFragment : Fragment() {


    private var _binding: FragmentApplyCampaignSucessBinding? = null
    private val binding get() = _binding!!
    private lateinit var campaignApplication: CampaignApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentApplyCampaignSucessBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(activity, AppAdActivity::class.java)
                startActivity(intent)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        val bundle = arguments
        bundle?.let {
            val args = ApplyCampaignSuccessFragmentArgs.fromBundle(it)
            if(args.campaignApplication != null){
                campaignApplication = args.campaignApplication
            }
            else{
                SnackbarHelper.showErrorSnackBar(requireView(),getString(R.string.an_error_occurred_network))
            }

        }

        setupOnClickListeners()

    }

    private fun setupOnClickListeners(){
        binding.tvBtnBackToHome.setOnClickListener {
            val intent = Intent(requireContext(),AppAdActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        binding.tvBtnCampaignDetails.setOnClickListener {
            val intent = Intent(requireContext(),CampaignDetailsActivity::class.java)
            intent.putExtra("campaign",campaignApplication.selectedCampaign)
            intent.putExtra("toCampaignDetails",true)
            intent.putExtra("enrolledCampaignId",campaignApplication.selectedCampaign.campaignId)
            startActivity(intent)
            requireActivity().finish()
        }

        binding.tvBtnInstallerDetails.setOnClickListener {
            val action = ApplyCampaignSuccessFragmentDirections.actionApplyCampaignSucessFragmentToApplyCampaignFinalFragment(campaignApplication)
            Navigation.findNavController(requireView()).navigate(action)

        }
    }


}