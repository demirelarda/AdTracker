package com.mycompany.advioo.ui.fragments.campaigndetails


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.RequestManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.mycompany.advioo.BuildConfig
import com.mycompany.advioo.R
import com.mycompany.advioo.adapters.CampaignImageAdapter
import com.mycompany.advioo.databinding.FragmentCampaignDetailsBinding
import com.mycompany.advioo.models.campaign.Campaign
import com.mycompany.advioo.models.campaignapplication.CampaignApplication
import com.mycompany.advioo.models.installer.Installer
import com.mycompany.advioo.ui.activities.AppAdActivity
import com.mycompany.advioo.util.SnackbarHelper
import com.mycompany.advioo.viewmodels.ApplyCampaignSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.Polyline
import java.io.File
import javax.inject.Inject


@AndroidEntryPoint
class CampaignDetailsFragment : Fragment() {

    private var _binding: FragmentCampaignDetailsBinding? = null
    private val binding get() = _binding!!
    private val campaignApplicationSharedViewModel: ApplyCampaignSharedViewModel by activityViewModels()
    private var campaign: Campaign = Campaign()
    private val authInstance = FirebaseAuth.getInstance()
    private lateinit var selectedCampaignLevel: String
    private lateinit var campaignImageAdapter: CampaignImageAdapter
    private lateinit var enrolledCampaign : String

    @Inject
    lateinit var glide: RequestManager



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCampaignDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        campaign = if (requireActivity().intent.hasExtra("campaign")) {
            requireActivity().intent.getParcelableExtra("campaign")!!
        } else {
            campaignApplicationSharedViewModel.campaignApplication.value?.selectedCampaign
                ?: Campaign()
        }

        if(requireActivity().intent.hasExtra("enrolledCampaignId")){
            enrolledCampaign = requireActivity().intent.getStringExtra("enrolledCampaignId")!!
        }

        if(requireActivity().intent.hasExtra("toInstallerDetails")){
            val campaignApplicationFromMyCampaigns : CampaignApplication = requireActivity().intent.getParcelableExtra("toInstallerDetails")!!
            campaign = campaignApplicationFromMyCampaigns.selectedCampaign
            enrolledCampaign = campaignApplicationFromMyCampaigns.selectedCampaign.campaignId
            val action = CampaignDetailsFragmentDirections.actionCampaignDetailsToApplyCampaignFinalFragment(campaignApplicationFromMyCampaigns,true)
            Navigation.findNavController(requireView()).navigate(action)
        }

        println("enrolled campaign id = $enrolledCampaign")
        println("campaign id = "+campaign.campaignId)

        if(enrolledCampaign.isNotEmpty()){
            if(campaign.campaignId == enrolledCampaign){
                //enrolled campaign
                binding.btnApplyCampaignDetails.isEnabled = false
                val drawable = ResourcesCompat.getDrawable(resources, R.drawable.disabled_button_background_gray, context?.theme)
                if (drawable != null) {
                    binding.btnApplyCampaignDetails.background = drawable
                }
            }
            else{
                //not enrolled campaign but disable buttons etc.
                binding.btnApplyCampaignDetails.isEnabled = false
                val drawable = ResourcesCompat.getDrawable(resources, R.drawable.disabled_button_background_gray, context?.theme)
                if (drawable != null) {
                    binding.btnApplyCampaignDetails.background = drawable
                }
            }
        }



        setupOnClickListeners()
        setupViews()
        setupMapView()
        setupViewPager()


    }

    private fun setupViewPager() {
        val images = mutableListOf<Any>()



        images.add(campaign.campaignImageURL)
        images.add(R.drawable.light_car_image)
        images.add(R.drawable.advanced_car_image)
        images.add(R.drawable.pro_car_image)

        campaignImageAdapter = CampaignImageAdapter(glide, images).also { adapter ->
            adapter.onPageClick = { position ->
                when (position) {
                    0 -> binding.radioGroup.clearCheck()
                    1 -> binding.radioButtonLight.isChecked = true
                    2 -> binding.radioButtonAdvanced.isChecked = true
                    3 -> binding.radioButtonPro.isChecked = true
                }
            }
        }

        binding.imageSliderCamaign.adapter = campaignImageAdapter

        binding.imageSliderCamaign.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> binding.radioGroup.clearCheck()
                    1 -> binding.radioButtonLight.isChecked = true
                    2 -> binding.radioButtonAdvanced.isChecked = true
                    3 -> binding.radioButtonPro.isChecked = true
                }
            }
        })
    }




    private fun setupOnClickListeners() {
        binding.ivBtnBackFromCampaignDetails.setOnClickListener {
            val intent = Intent(requireContext(), AppAdActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        binding.tvCampaignMapGoToFullPage.setOnClickListener {
            val action = CampaignDetailsFragmentDirections.actionCampaignDetailsToFullMapFragment(null,campaign.mapBorderLocationList.toTypedArray())
            Navigation.findNavController(requireView()).navigate(action)
        }

        binding.btnApplyCampaignDetails.setOnClickListener {
            if (validateRadioGroup()) {
                val campaignApplicationObject = CampaignApplication(
                    applicationId = "",
                    applicantId = authInstance.currentUser?.uid ?: "",
                    applicantFullName = authInstance.currentUser?.displayName ?: "",
                    applicationDate = Timestamp.now(),
                    selectedInstaller = Installer(),
                    selectedCampaign = campaign,
                    selectedCampaignLevel = selectedCampaignLevel

                )

                campaignApplicationSharedViewModel.setApplicantId(authInstance.currentUser?.uid!!)
                campaignApplicationSharedViewModel.setApplicantFullName(authInstance.currentUser?.displayName!!)
                campaignApplicationSharedViewModel.setSelectedCampaign(campaign)
                campaignApplicationSharedViewModel.setSelectedCampaignLevel(selectedCampaignLevel)


                val action =
                    CampaignDetailsFragmentDirections.actionCampaignDetailsToAvailableInstallersFragment(
                        campaignApplicationObject
                    )
                findNavController().navigate(action)
            }

        }
    }

    private fun setupViews() {

        if (!(campaign.availableCampaignPlans.contains(1))) {
            binding.radioButtonAdvanced.visibility = View.GONE
            binding.tvAdvancedPrice.visibility = View.GONE
        }
        if (!(campaign.availableCampaignPlans.contains(2))) {
            binding.radioButtonPro.visibility = View.GONE
            binding.tvProPrice.visibility = View.GONE
        }
        binding.radioButtonLight.setOnClickListener { updatePriceTextColors() }
        binding.radioButtonAdvanced.setOnClickListener { updatePriceTextColors() }
        binding.radioButtonPro.setOnClickListener { updatePriceTextColors() }
        updatePriceTextColors()
        //binding.tvCityCampaignDetails.text = campaign.city
        binding.tvLightPrice.text = campaign.campaignLightPaymentRange
        binding.tvProPrice.text = campaign.campaignProPaymentRange
        binding.tvAdvancedPrice.text = campaign.campaignAdvPaymentRange
        binding.tvCampaignDetailsTitle.text = campaign.campaignTitle
    }


    private fun validateRadioGroup(): Boolean {
        return if (binding.radioGroup.checkedRadioButtonId == -1) {
            SnackbarHelper.showErrorSnackBar(
                requireView(),
                getString(R.string.please_select_a_campaign_level)
            )
            false
        } else {
            val selectedRadioButtonId = binding.radioGroup.checkedRadioButtonId
            val selectedRadioButton =
                requireActivity().findViewById<RadioButton>(selectedRadioButtonId)
            selectedCampaignLevel = selectedRadioButton.text.toString()
            true
        }
    }

    private fun setupMapView() {
        val osmConfig = Configuration.getInstance()
        val basePath = File(requireActivity().cacheDir.absolutePath, "osmdroid")
        osmConfig.osmdroidBasePath = basePath
        osmConfig.osmdroidTileCache = File(osmConfig.osmdroidBasePath, "tiles")
        osmConfig.userAgentValue = "${BuildConfig.APPLICATION_ID}/${BuildConfig.VERSION_NAME}"

        binding.mapViewCampaignDetails.setTileSource(TileSourceFactory.MAPNIK)
        binding.mapViewCampaignDetails.setMultiTouchControls(true)

        val points = campaign.mapBorderLocationList.map { GeoPoint(it.latitude, it.longitude) }

        drawPolygonBoundary(points)
        fillPolygon(points)

        val mapController = binding.mapViewCampaignDetails.controller
        mapController.setZoom(11.0)
        val location = GeoPoint(points[0].latitude, points[0].longitude)


        // Center the map to the location
        mapController.setCenter(location)
    }

    private fun drawPolygonBoundary(points: List<GeoPoint>) {
        val polyline = Polyline()
        polyline.apply {
            for (point in points) {
                addPoint(point)
            }
            addPoint(points.first())
            color = Color.RED
            width = 5f
        }

        binding.mapViewCampaignDetails.overlays.add(polyline)
        binding.mapViewCampaignDetails.invalidate()
    }

    private fun fillPolygon(points: List<GeoPoint>) {
        val polygon = Polygon().apply {
            for (point in points) {
                addPoint(point)
            }
            fillColor = Color.parseColor("#40FF8080")
            strokeWidth = 0f
        }

        binding.mapViewCampaignDetails.overlays.add(polygon)
        binding.mapViewCampaignDetails.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updatePriceTextColors() {
        val lightBlack = Color.parseColor("#808080")
        val normalBlack = Color.parseColor("#FF000000")

        if (binding.radioButtonLight.isChecked) {
            binding.imageSliderCamaign.currentItem = 1
            binding.tvLightPrice.setTextColor(Color.BLACK)
            binding.tvAdvancedPrice.setTextColor(lightBlack)
            binding.tvProPrice.setTextColor(lightBlack)
            binding.radioButtonAdvanced.setTextColor(lightBlack)
            binding.radioButtonPro.setTextColor(lightBlack)
            binding.radioButtonLight.setTextColor(normalBlack)
        } else if (binding.radioButtonAdvanced.isChecked) {
            binding.imageSliderCamaign.currentItem = 2
            binding.tvLightPrice.setTextColor(lightBlack)
            binding.tvAdvancedPrice.setTextColor(Color.BLACK)
            binding.tvProPrice.setTextColor(lightBlack)
            binding.radioButtonPro.setTextColor(lightBlack)
            binding.radioButtonLight.setTextColor(lightBlack)
            binding.radioButtonAdvanced.setTextColor(normalBlack)
        } else if (binding.radioButtonPro.isChecked) {
            binding.imageSliderCamaign.currentItem = 3
            binding.tvLightPrice.setTextColor(lightBlack)
            binding.tvAdvancedPrice.setTextColor(lightBlack)
            binding.tvProPrice.setTextColor(Color.BLACK)
            binding.radioButtonAdvanced.setTextColor(lightBlack)
            binding.radioButtonLight.setTextColor(lightBlack)
            binding.radioButtonPro.setTextColor(normalBlack)
        }
    }
}






