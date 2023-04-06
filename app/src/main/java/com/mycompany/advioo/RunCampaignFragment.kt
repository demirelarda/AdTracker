package com.mycompany.advioo

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.google.android.gms.location.*
import com.mycompany.advioo.databinding.FragmentRunCampaignBinding
import com.mycompany.advioo.util.SnackbarHelper
import com.mycompany.advioo.viewmodels.RunCampaignViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RunCampaignFragment @Inject constructor(
    val locationManager: LocationManager
) : Fragment() {

    private var _binding: FragmentRunCampaignBinding? = null
    private val binding get() = _binding!!

    private val runCampaignViewModel: RunCampaignViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var isCalculating = false // flag to check if distance calculation is active
    private var previousLocation: Location? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRunCampaignBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
        checkLocationEnabled()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // set up location callback
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (isCalculating) { // only calculate distance if flag is true
                    val location = locationResult.lastLocation
                    if (previousLocation != null) {
                        if (location != null) {
                            runCampaignViewModel.calculateDistance(
                                previousLocation!!.latitude,
                                previousLocation!!.longitude,
                                location.latitude,
                                location.longitude
                            )
                        }
                    }
                    previousLocation = location
                }
            }
        }

        // set up Start Calculation button click listener
        binding.btnStartTrip.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            } else {
                isCalculating = true // set flag to true
                fusedLocationClient.requestLocationUpdates(
                    LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,
                        1000)
                        .setWaitForAccurateLocation(false)
                        .setMinUpdateIntervalMillis(1000)
                        .setMaxUpdateDelayMillis(1000)
                        .build(),
                    locationCallback,
                    Looper.getMainLooper()
                )
                binding.btnStartTrip.isEnabled = false // disable button
                binding.btnStartTrip.visibility = View.GONE
                binding.btnStopTrip.isEnabled = true // enable Stop Calculation button
                binding.btnStopTrip.visibility = View.VISIBLE
            }
        }

        // set up Stop Calculation button click listener
        binding.btnStopTrip.setOnClickListener {
            isCalculating = false // set flag to false
            fusedLocationClient.removeLocationUpdates(locationCallback)
            binding.btnStartTrip.isEnabled = true // enable Start Calculation button
            binding.btnStartTrip.visibility = View.VISIBLE
            binding.btnStopTrip.isEnabled = false // disable button
            binding.btnStopTrip.visibility = View.GONE
            runCampaignViewModel.resetDistance() // reset total distance
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                binding.btnStartTrip.performClick() // simulate click event
            } else {
                SnackbarHelper.showErrorSnackBar(
                    requireView(),
                    getString(R.string.permission_needed_location)
                )
            }
        }
    }

    private fun checkLocationEnabled() {
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!isGpsEnabled) {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.location_needed))
                .setMessage(getString(R.string.permission_needed_location))
                .setPositiveButton(getString(R.string.open_location_settings)) { _, _ ->
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        checkLocationEnabled() // Check if location is enabled when the fragment is resumed
    }

    private fun subscribeToObservers() {
        runCampaignViewModel.distanceDriven.observe(viewLifecycleOwner) { distanceDriven ->
            if (distanceDriven == null) {
                binding.tvKMDriven.text = "0"
            }
            val formattedDistance = String.format("%.2f", distanceDriven)
            binding.tvKMDriven.text = formattedDistance + getString(R.string.length_unit)
        }
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}


