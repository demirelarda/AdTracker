package com.mycompany.advioo.ui.fragments.campaigndetails

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.Timestamp
import com.mycompany.advioo.AdviooApplication
import com.mycompany.advioo.BuildConfig
import com.mycompany.advioo.R
import com.mycompany.advioo.databinding.FragmentApplyCampaignFinalBinding
import com.mycompany.advioo.models.campaignapplication.CampaignApplication
import com.mycompany.advioo.util.SnackbarHelper
import com.mycompany.advioo.viewmodels.ApplyCampaignSharedViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import java.io.File
import java.util.UUID


class ApplyCampaignFinalFragment : Fragment() {


    private var _binding: FragmentApplyCampaignFinalBinding? = null
    private val binding get() = _binding!!
    private val campaignApplicationSharedViewModel: ApplyCampaignSharedViewModel by activityViewModels()
    private var campaignApplication = CampaignApplication()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentApplyCampaignFinalBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        campaignApplication = campaignApplicationSharedViewModel.campaignApplication.value!!
        setupViews()
        setupOnClickListeners()
        setupMapView()

    }



    private fun setupViews(){
        binding.tvInstallerNameLastEnroll.text = campaignApplication.selectedInstaller.installerName
        binding.tvInstallerAddress.text = campaignApplication.selectedInstaller.installerAddress
        binding.tvInstallerPhoneNumber.text = campaignApplication.selectedInstaller.installerPhoneNumber
    }

    private fun setupOnClickListeners(){
        binding.tvGetDirectionsForInstaller.setOnClickListener {
            showDirections(requireContext(),campaignApplication.selectedInstaller.installerCoordinates.latitude,campaignApplication.selectedInstaller.installerCoordinates.longitude)
        }

        binding.ivBtnBackFromCompleteEnrollment.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnEnrollCampaignFinal.setOnClickListener {
            enrollCampaign()
        }
    }


    private fun enrollCampaign(){
        if(binding.cboxSelectThisInstallerLast.isChecked){
            val uuid = UUID.randomUUID()
            campaignApplicationSharedViewModel.setApplicationId(uuid.toString())
            val application : Application = AdviooApplication()
            val currentTime = (application as AdviooApplication).trueTime.now()
            campaignApplicationSharedViewModel.setApplicationDate(Timestamp(currentTime))
            campaignApplicationSharedViewModel.enrollCampaign()
        }
        else{
            SnackbarHelper.showErrorSnackBar(requireView(),getString(R.string.please_confirm_installer))
        }

    }



    private fun showDirections(context: Context, latitude: Double, longitude: Double) {
        val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)

        // Google maps package name
        mapIntent.setPackage("com.google.android.apps.maps")

        if (mapIntent.resolveActivity(context.packageManager) != null) {
            // If google maps is installed open in app
            context.startActivity(mapIntent)
        } else {
            // If google maps is not installed, open in browser
            val browserIntent = Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps?q=$latitude,$longitude"))
            context.startActivity(browserIntent)
        }
    }


    private fun setupMapView(){

        val osmConfig = Configuration.getInstance()
        val basePath = File(requireActivity().cacheDir.absolutePath, "osmdroid")
        osmConfig.osmdroidBasePath = basePath
        osmConfig.osmdroidTileCache = File(osmConfig.osmdroidBasePath, "tiles")
        osmConfig.userAgentValue = "${BuildConfig.APPLICATION_ID}/${BuildConfig.VERSION_NAME}"

        binding.mapViewLastEnrollment.setTileSource(TileSourceFactory.MAPNIK)
        binding.mapViewLastEnrollment.setMultiTouchControls(true)
        val mapController = binding.mapViewLastEnrollment.controller
        mapController.setZoom(11.0)
        val latitude = campaignApplication.selectedInstaller.installerCoordinates.latitude
        val longitude = campaignApplication.selectedInstaller.installerCoordinates.longitude
        val location = GeoPoint(latitude,longitude)
        mapController.setCenter(location)
        val marker = Marker(binding.mapViewLastEnrollment)
        marker.position = GeoPoint(latitude,longitude)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = campaignApplication.selectedInstaller.installerName + "\n" + campaignApplication.selectedInstaller.installerAddress
        binding.mapViewLastEnrollment.overlays.add(marker)

    }





}