package com.mycompany.advioo.ui.fragments.campaigns


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.RequestManager
import com.mycompany.advioo.R
import com.mycompany.advioo.databinding.FragmentMyCampaignsBinding
import com.mycompany.advioo.models.campaignapplication.CampaignApplication
import com.mycompany.advioo.ui.activities.CampaignDetailsActivity
import com.mycompany.advioo.viewmodels.MyCampaignsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyCampaignsFragment : Fragment() {

    private var _binding: FragmentMyCampaignsBinding? = null
    private val binding get() = _binding!!
    private val myCampaignsViewModel : MyCampaignsViewModel by viewModels(ownerProducer = { this } )
    private var campaignApplication : CampaignApplication = CampaignApplication()

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
        myCampaignsViewModel.getCampaignApplication()
        subscribeToObservers()
        setupOnClickListeners()

    }

    private fun setupViews(){
        binding.tvCampaignNameMyCampaigns.text = campaignApplication.selectedCampaign.campaignTitle
        binding.llMyCampaigns.visibility = View.VISIBLE
        binding.tvErrorHaveNotEnrolledCampaign.visibility = View.GONE
        binding.tvCampaignLevelMyCampaigns.text = campaignApplication.selectedCampaignLevel
        if(campaignApplication.started){
            binding.tvStartResumeCampaignMyCampaigns.text = getString(R.string.resume_campaign)
        }
        if(campaignApplication.status == 0){
            binding.tvStartResumeCampaignMyCampaigns.visibility = View.GONE
            binding.tvCampaignApplicationStatus.text = getString(R.string.waiting_for_installment)
            binding.tvCampaignApplicationStatus.setTextColor(resources.getColor(R.color.colorSnackBarError))
        }
        else{
            if(campaignApplication.status == 1){
                binding.tvStartResumeCampaignMyCampaigns.visibility = View.VISIBLE
                binding.tvCampaignApplicationStatus.text = getString(R.string.ready_to_start)
                binding.tvCampaignApplicationStatus.setTextColor(resources.getColor(R.color.app_blue))
            }
            if(campaignApplication.status == 1 && campaignApplication.started){
                binding.tvStartResumeCampaignMyCampaigns.visibility = View.VISIBLE
                binding.tvCampaignApplicationStatus.text = getString(R.string.in_progress)
                binding.tvCampaignApplicationStatus.setTextColor(resources.getColor(R.color.app_green))
            }
        }
        glide.load(campaignApplication.selectedCampaign.campaignImageURL).into(binding.ivMyCampaignsCampaign)
    }

    private fun setupOnClickListeners(){
        binding.tvSeeInstallersDetailsMyCampaigns.setOnClickListener {
            val intent = Intent(requireContext(),CampaignDetailsActivity::class.java)
            intent.putExtra("toInstallerDetails",campaignApplication)
            startActivity(intent)
        }
        binding.tvSeeCampaignDetailsMyCampaigns.setOnClickListener {
            val intent = Intent(requireContext(),CampaignDetailsActivity::class.java)
            intent.putExtra("campaign",campaignApplication.selectedCampaign)
            intent.putExtra("toCampaignDetails",true)
            intent.putExtra("enrolledCampaignId",campaignApplication.selectedCampaign.campaignId)
            startActivity(intent)
        }
    }


    private fun subscribeToObservers(){
        myCampaignsViewModel.campaignApplication.observe(viewLifecycleOwner){
            campaignApplication = it
            setupViews()
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
                binding.progressbarMyCampaigns.visibility = View.GONE
            }
        }

        myCampaignsViewModel.campaignApplicationIsEmpty.observe(viewLifecycleOwner){campaignIsEmpty->
            if(campaignIsEmpty){
                binding.llMyCampaigns.visibility = View.GONE
                binding.tvErrorHaveNotEnrolledCampaign.visibility = View.VISIBLE
                binding.progressbarMyCampaigns.visibility = View.GONE
            }
        }
    }


}