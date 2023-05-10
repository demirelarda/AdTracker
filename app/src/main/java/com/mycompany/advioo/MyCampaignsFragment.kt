package com.mycompany.advioo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.mycompany.advioo.databinding.FragmentMyCampaignsBinding
import com.mycompany.advioo.viewmodels.MyCampaignsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyCampaignsFragment : Fragment() {

    private var _binding: FragmentMyCampaignsBinding? = null
    private val binding get() = _binding!!
    private val myCampaignsViewModel : MyCampaignsViewModel by viewModels(ownerProducer = { this } )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMyCampaignsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
        myCampaignsViewModel.getCampaignApplication()
    }


    private fun subscribeToObservers(){
        myCampaignsViewModel.campaignApplication.observe(viewLifecycleOwner){
            println("observed = "+it.selectedCampaign)
            binding.tvCampaignNameMyCampaigns.text = it.selectedCampaign.campaignTitle
        }
    }


}