package com.mycompany.advioo

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.bumptech.glide.RequestManager
import com.mycompany.advioo.databinding.FragmentMyCampaignsBinding
import com.mycompany.advioo.models.campaignapplication.CampaignApplication
import com.mycompany.advioo.models.localapplication.LocalCampaignApplication
import com.mycompany.advioo.ui.activities.AppAdActivity
import com.mycompany.advioo.util.SnackbarHelper
import com.mycompany.advioo.viewmodels.MyCampaignsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyCampaignsFragment : Fragment() {

    private var _binding: FragmentMyCampaignsBinding? = null
    private val binding get() = _binding!!
    private val myCampaignsViewModel : MyCampaignsViewModel by viewModels(ownerProducer = { this } )
    private lateinit var campaignApplication: LocalCampaignApplication

    @Inject
    lateinit var glide: RequestManager

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
        binding.tvStartResumeCampaignMyCampaigns.setOnClickListener {
            val intent = Intent(requireContext(),AppAdActivity::class.java)
            intent.putExtra("toRunCampaign",true)
            startActivity(intent)
        }
    }

    private fun setupViews(localCampaignApplication: LocalCampaignApplication){
        binding.tvCampaignNameMyCampaigns.text = localCampaignApplication.selectedCampaign.campaignTitle
        glide.load(localCampaignApplication.selectedCampaign.campaignImageURL).into(binding.ivMyCampaignsCampaign)
    }


    private fun subscribeToObservers(){
        myCampaignsViewModel.campaignApplication.observe(viewLifecycleOwner){localCampaignApplication->
            campaignApplication = localCampaignApplication
            setupViews(localCampaignApplication)
        }

        myCampaignsViewModel.loadingState.observe(viewLifecycleOwner){loading->
            if(loading){
                binding.llMyCampaigns.visibility = View.GONE
                binding.progressbarMyCampaigns.visibility = View.VISIBLE
            }
            else{
                binding.llMyCampaigns.visibility = View.VISIBLE
                binding.progressbarMyCampaigns.visibility = View.GONE
            }
        }

        myCampaignsViewModel.failState.observe(viewLifecycleOwner){fail->
            if(fail){
                binding.llMyCampaigns.visibility = View.GONE
                SnackbarHelper.showErrorSnackBar(requireView(),getString(R.string.an_error_occurred_network))
            }
        }
    }


}