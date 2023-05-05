package com.mycompany.advioo.ui.fragments.campaigndetails

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.mycompany.advioo.BuildConfig
import com.mycompany.advioo.R
import com.mycompany.advioo.databinding.FragmentFullMapBinding
import com.mycompany.advioo.models.campaign.LatLngPoint
import com.mycompany.advioo.models.installer.Installer
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.Polyline
import java.io.File


class FullMapFragment : Fragment() {


    private var _binding: FragmentFullMapBinding? = null
    private val binding get() = _binding!!
    private var installerListMap : ArrayList<Installer> = ArrayList()
    private var boundaryList: ArrayList<LatLngPoint> = ArrayList()
    private var mapType : Int = 0
    //private var installerCoordinateList: ArrayList<LatLngPoint> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFullMapBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOnClickListeners()

        val bundle = arguments

        if(bundle!=null){
            val args = FullMapFragmentArgs.fromBundle(bundle)
            if(args.installerListMap != null){
                binding.tvFullMapTitle.text = getString(R.string.available_installers)
                installerListMap.addAll(args.installerListMap)
                mapType = 0
            }
            if(args.campaignBorderListFullMap != null){
                binding.tvFullMapTitle.text = getString(R.string.campaign_map)
                boundaryList.addAll(args.campaignBorderListFullMap)
                mapType = 1
            }
        }


        setupMapView(mapType)
    }


    private fun setupMapView(mapType: Int) {
        val osmConfig = Configuration.getInstance()
        val basePath = File(requireActivity().cacheDir.absolutePath, "osmdroid")
        osmConfig.osmdroidBasePath = basePath
        osmConfig.osmdroidTileCache = File(osmConfig.osmdroidBasePath, "tiles")
        osmConfig.userAgentValue = "${BuildConfig.APPLICATION_ID}/${BuildConfig.VERSION_NAME}"

        binding.mapviewFullMap.setTileSource(TileSourceFactory.MAPNIK)
        binding.mapviewFullMap.setMultiTouchControls(true)
        val mapController = binding.mapviewFullMap.controller
        if(mapType == 0){
            val coordinatesList : ArrayList<LatLngPoint> = ArrayList()
            for(installer in installerListMap){
                coordinatesList.add(installer.installerCoordinates)
            }
            val points = coordinatesList.map { GeoPoint(it.latitude, it.longitude) }
            mapController.setZoom(11.0)
            val location = GeoPoint(points[0].latitude, points[0].longitude) //center the map on first installer point in the list
            mapController.setCenter(location)
            addMarkersToMap(binding.mapviewFullMap,installerListMap)
        }

        if(mapType == 1){
            val boundaryPoints = boundaryList.map { GeoPoint(it.latitude,it.longitude) }
            mapController.setZoom(12.0)
            val location = GeoPoint(boundaryList[1].latitude,boundaryList[1].longitude)
            mapController.setCenter(location)
            drawPolygonBoundary(boundaryPoints)
            fillPolygon(boundaryPoints)
        }



    }

    private fun setupOnClickListeners(){
        binding.ivBtnBackFromFullMap.setOnClickListener {
            findNavController().popBackStack()
        }
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

        binding.mapviewFullMap.overlays.add(polyline)
        binding.mapviewFullMap.invalidate()
    }

    private fun fillPolygon(points: List<GeoPoint>) {
        val polygon = Polygon().apply {
            for (point in points) {
                addPoint(point)
            }
            fillColor = Color.parseColor("#40FF8080")
            strokeWidth = 0f
        }

        binding.mapviewFullMap.overlays.add(polygon)
        binding.mapviewFullMap.invalidate()
    }

    private fun addMarkersToMap(mapView: MapView, installerList: List<Installer>) {
        for (installer in installerList) {
            val marker = Marker(mapView)
            val latitude = installer.installerCoordinates.latitude
            val longitude = installer.installerCoordinates.longitude
            marker.position = GeoPoint(latitude,longitude)
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.relatedObject = installer
            marker.title = installer.installerName + "\n" + installer.installerAddress

            mapView.overlays.add(marker)
        }
    }



}