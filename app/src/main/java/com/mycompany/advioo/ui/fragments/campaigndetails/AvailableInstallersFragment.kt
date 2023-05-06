package com.mycompany.advioo.ui.fragments.campaigndetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.mycompany.advioo.R
import com.mycompany.advioo.adapters.InstallerListAdapter
import com.mycompany.advioo.databinding.FragmentAvailableInstallersBinding
import com.mycompany.advioo.models.campaign.LatLngPoint
import com.mycompany.advioo.models.campaignapplication.CampaignApplication
import com.mycompany.advioo.models.installer.Installer
import com.mycompany.advioo.models.user.UserCity
import com.mycompany.advioo.util.SnackbarHelper
import com.mycompany.advioo.viewmodels.ApplyCampaignSharedViewModel
import com.mycompany.advioo.viewmodels.AvailableInstallersViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class AvailableInstallersFragment : Fragment() {

    private var _binding: FragmentAvailableInstallersBinding? = null
    private val binding get() = _binding!!
    private var campaignApplication: CampaignApplication = CampaignApplication()
    private val campaignApplicationSharedViewModel: ApplyCampaignSharedViewModel by activityViewModels()
    private val availableInstallersViewModel: AvailableInstallersViewModel by viewModels(ownerProducer = { this })
    private val installerList : ArrayList<Installer> = arrayListOf()

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var installerListAdapter: InstallerListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAvailableInstallersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        if (bundle != null) {
            val args = AvailableInstallersFragmentArgs.fromBundle(bundle)
            campaignApplication = args.campaignApplicationObject
                ?: (campaignApplicationSharedViewModel.campaignApplication.value
                    ?: CampaignApplication())
        }

        installerListAdapter = InstallerListAdapter()


        subscribeToObservers()
        //get installers in current campaign state
        availableInstallersViewModel.getInstallersByState(campaignApplication.selectedCampaign.selectedCities[0].stateId)
        setupViews()
        setupOnClickListeners()
    }


    private fun setupOnClickListeners() {

        binding.ivBtnBackFromAvailableInstallers.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvSeeInstallersOnMap.setOnClickListener {

            val action = AvailableInstallersFragmentDirections.actionAvailableInstallersFragmentToFullMapFragment(installerList.toTypedArray())
            Navigation.findNavController(requireView()).navigate(action)
        }

        installerListAdapter.setOnItemClickListener {installer->
            campaignApplication.selectedInstaller = installer
            campaignApplicationSharedViewModel.setSelectedInstaller(installer)
            val action = AvailableInstallersFragmentDirections.actionAvailableInstallersFragmentToApplyCampaignFinalFragment()
            Navigation.findNavController(requireView()).navigate(action)
        }


    }

    private fun setupViews() {
        binding.tvSelectedCampaignName.text = campaignApplication.selectedCampaign.campaignTitle
        binding.tvSelectedCampaignLevel.text = campaignApplication.selectedCampaignLevel
        glide.load(campaignApplication.selectedCampaign.campaignImageURL)
            .into(binding.ivCampaignInstallerDetailsImage)
        binding.rvInstallersList.adapter = installerListAdapter
        binding.rvInstallersList.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun subscribeToObservers(){
        availableInstallersViewModel.loadingState.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBarAvailableInstallers.visibility = View.VISIBLE
                binding.scrollViewAvailableInstallers.visibility = View.GONE
            } else {
                binding.progressBarAvailableInstallers.visibility = View.GONE
                binding.scrollViewAvailableInstallers.visibility = View.VISIBLE
            }
        }

        availableInstallersViewModel.failState.observe(viewLifecycleOwner) { isFail ->
            if (isFail) {
                SnackbarHelper.showErrorSnackBar(requireView(),getString(R.string.failed_to_load_installers))
            }
        }

        availableInstallersViewModel.installerList.observe(viewLifecycleOwner){installers->
            installerListAdapter.installers = installers
            if(installers.isEmpty()){
                binding.tvSeeInstallersOnMap.isClickable = false
            }
            installerList.addAll(installers)
        }
    }

}