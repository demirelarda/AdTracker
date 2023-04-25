package com.mycompany.advioo.ui.fragments.campaigndetails


import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.RequestManager
import com.mycompany.advioo.BuildConfig
import com.mycompany.advioo.R
import com.mycompany.advioo.databinding.FragmentCampaignDetailsBinding
import com.mycompany.advioo.models.campaign.Campaign
import com.mycompany.advioo.viewmodels.CampaignDetailsViewModel
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
    private val campaignDetailsViewModel: CampaignDetailsViewModel by viewModels(ownerProducer = { this })
    private var campaign: Campaign = Campaign()
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
        if (requireActivity().intent.hasExtra("campaign")) {
            campaign = requireActivity().intent.getParcelableExtra("campaign")!!

            binding.radioButtonLight.setOnClickListener { updatePriceTextColors() }
            binding.radioButtonAdvanced.setOnClickListener { updatePriceTextColors() }
            binding.radioButtonPro.setOnClickListener { updatePriceTextColors() }
            updatePriceTextColors()
            binding.tvCityCampaignDetails.text = campaign.city
            binding.tvLightPrice.text = resources.getString(R.string.currency)+campaign.campaignLightPaymentRange
            binding.tvProPrice.text = resources.getString(R.string.currency)+campaign.campaignProPaymentRange
            binding.tvAdvancedPrice.text = resources.getString(R.string.currency)+campaign.campaignAdvancedPaymentRange
            binding.tvCampaignDetailsTitle.text = campaign.campaignTitle
            glide.load(campaign.campaignImageURL).into(binding.ivCampaignDetailsImage)
            binding.tvCampaignMapGoToFullPage.setOnClickListener {

            }
        }

        setupMapView()
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
            binding.tvLightPrice.setTextColor(Color.BLACK)
            binding.tvAdvancedPrice.setTextColor(lightBlack)
            binding.tvProPrice.setTextColor(lightBlack)
            binding.radioButtonAdvanced.setTextColor(lightBlack)
            binding.radioButtonPro.setTextColor(lightBlack)
            binding.radioButtonLight.setTextColor(normalBlack)
        } else if (binding.radioButtonAdvanced.isChecked) {
            binding.tvLightPrice.setTextColor(lightBlack)
            binding.tvAdvancedPrice.setTextColor(Color.BLACK)
            binding.tvProPrice.setTextColor(lightBlack)
            binding.radioButtonPro.setTextColor(lightBlack)
            binding.radioButtonLight.setTextColor(lightBlack)
            binding.radioButtonAdvanced.setTextColor(normalBlack)
        } else if (binding.radioButtonPro.isChecked) {
            binding.tvLightPrice.setTextColor(lightBlack)
            binding.tvAdvancedPrice.setTextColor(lightBlack)
            binding.tvProPrice.setTextColor(Color.BLACK)
            binding.radioButtonAdvanced.setTextColor(lightBlack)
            binding.radioButtonLight.setTextColor(lightBlack)
            binding.radioButtonPro.setTextColor(normalBlack)
        }
    }
}






