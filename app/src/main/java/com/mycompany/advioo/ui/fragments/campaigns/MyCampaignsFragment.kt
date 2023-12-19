package com.mycompany.advioo.ui.fragments.campaigns


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.bumptech.glide.RequestManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mycompany.advioo.R
import com.mycompany.advioo.ui.activities.RunCampaignActivity
import com.mycompany.advioo.databinding.FragmentMyCampaignsBinding
import com.mycompany.advioo.models.campaignapplication.CampaignApplication
import com.mycompany.advioo.ui.activities.CampaignDetailsActivity
import com.mycompany.advioo.ui.activities.PhotoActivity
import com.mycompany.advioo.ui.fragments.CampaignStatsFragment
import com.mycompany.advioo.util.SnackbarHelper
import com.mycompany.advioo.util.Util.ACTION_NORMAL_TRACKING_FRAGMENT
import com.mycompany.advioo.viewmodels.MyCampaignsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val BATTERY_OPTIMIZATION_REQUEST_CODE = 2

@AndroidEntryPoint
class MyCampaignsFragment : Fragment(){

    private var _binding: FragmentMyCampaignsBinding? = null
    private val binding get() = _binding!!
    private val myCampaignsViewModel : MyCampaignsViewModel by viewModels(ownerProducer = { this } )
    private var campaignApplication : CampaignApplication = CampaignApplication()
    private var isLocationPermissionGranted = false
    private var isNotificationPermissionGranted = false
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    @Inject
    lateinit var glide: RequestManager



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private fun requestPermissions(){
        isLocationPermissionGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            isNotificationPermissionGranted = ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        }

        val permissionRequest : MutableList<String> = ArrayList()
        if(!isLocationPermissionGranted){
            permissionRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!isNotificationPermissionGranted){
                permissionRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        if(permissionRequest.isNotEmpty()){
            permissionLauncher.launch(permissionRequest.toTypedArray())
        }

    }

    private fun checkBatteryOptimizationPermission() : Boolean {
        val powerManager = requireActivity().getSystemService(Context.POWER_SERVICE) as PowerManager
        val packageName = requireActivity().packageName
        return powerManager.isIgnoringBatteryOptimizations(packageName)
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
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavView).visibility = View.VISIBLE
        subscribeToObservers()
        setupOnClickListeners()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            isNotificationPermissionGranted = true
        }
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){permissions->
            isLocationPermissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: isLocationPermissionGranted
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                isNotificationPermissionGranted =
                    permissions[Manifest.permission.POST_NOTIFICATIONS]
                        ?: isNotificationPermissionGranted
            }
            if (isLocationPermissionGranted && isNotificationPermissionGranted) {
                navigateToRunCampaign()
            } else {
                val errorPermissionList: ArrayList<String> = arrayListOf()
                if(!(isNotificationPermissionGranted)){
                    errorPermissionList.add("Notifications")
                }
                if(!(isLocationPermissionGranted)){
                    errorPermissionList.add("Precise Location")
                }
                binding.tvPermissionError.visibility = View.VISIBLE
                binding.tvPermissionErrorList.visibility = View.VISIBLE
                binding.tvPermissionErrorList.text = errorPermissionList.toString()
                SnackbarHelper.showErrorSnackBar(requireView(),(getString(R.string.the_following_permissions_denied)+errorPermissionList.toString()))
            }
        }

        requestPermissions()

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
            binding.tvStartResumeCampaignMyCampaigns.visibility = View.VISIBLE
            binding.tvStartResumeCampaignMyCampaigns.text = getString(R.string.upload_photos_to_start)
            binding.tvStartResumeCampaignMyCampaigns.setTextColor(resources.getColor(R.color.colorSnackBarError))
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

        binding.tvStartResumeCampaignMyCampaigns.setOnClickListener {
            if(campaignApplication.status == 1) {
                requestPermissions()
                if (isNotificationPermissionGranted && isLocationPermissionGranted) {
                    navigateToRunCampaign()
                } else {
                    val errorPermissionList: ArrayList<String> = arrayListOf()
                    if (!(isNotificationPermissionGranted)) {
                        errorPermissionList.add(getString(R.string.notifications))
                    }
                    if (!(isLocationPermissionGranted)) {
                        errorPermissionList.add(getString(R.string.precise_location))
                    }
                    binding.tvPermissionError.visibility = View.VISIBLE
                    binding.tvPermissionErrorList.visibility = View.VISIBLE
                    binding.tvPermissionErrorList.text = errorPermissionList.toString()
                    SnackbarHelper.showErrorSnackBar(
                        requireView(),
                        (getString(R.string.the_following_permissions_denied) + errorPermissionList.toString())
                    )

                }
            }
            else if(campaignApplication.status == 0){
                val intent = Intent(requireContext(),PhotoActivity::class.java)
                intent.putExtra("campaignApplication",campaignApplication)
                startActivity(intent)
                requireActivity().finish()
            }
        }

        binding.tvSeeStatsMyCampaigns.setOnClickListener {
            val campaignId = campaignApplication.selectedCampaign.campaignId
            val fragment = CampaignStatsFragment().apply {
                arguments = Bundle().apply {
                    putString("campaignId", campaignId)
                }
            }
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.mainFrameLayout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

    }



    private fun subscribeToObservers(){
        myCampaignsViewModel.campaignApplication.observe(viewLifecycleOwner){
            campaignApplication = it
            if(campaignApplication.status != 9){ //state 9 -> campaign ended state
                setupViews()
            }
            else{
                binding.llMyCampaigns.visibility = View.GONE
                binding.tvErrorHaveNotEnrolledCampaign.visibility = View.VISIBLE
                binding.progressbarMyCampaigns.visibility = View.GONE
            }
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

    private fun navigateToRunCampaign(){
        if(campaignApplication.status == 1) {
            if (checkBatteryOptimizationPermission()) {
                val intent = Intent(requireContext(), RunCampaignActivity::class.java)
                intent.action = ACTION_NORMAL_TRACKING_FRAGMENT
                intent.putExtra("campaignApplication", campaignApplication)
                startActivity(intent)
            } else {
                requestBatteryOptimizationPermission()
            }
        }
        else if(campaignApplication.status == 0){
            val intent = Intent(requireContext(),PhotoActivity::class.java)
            intent.putExtra("campaignApplication",campaignApplication)
            startActivity(intent)
            requireActivity().finish()
        }

    }

    private fun requestBatteryOptimizationPermission() {
        val powerManager = requireActivity().getSystemService(Context.POWER_SERVICE) as PowerManager
        val packageName = requireActivity().packageName
        if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {//TODO: CHECK BATTERY OPTIMIZATION RULES
                data = Uri.parse("package:$packageName")
            }
            startActivityForResult(intent, BATTERY_OPTIMIZATION_REQUEST_CODE)
        } else {
            navigateToRunCampaign()
        }
    }


}