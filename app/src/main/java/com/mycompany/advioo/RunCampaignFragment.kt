package com.mycompany.advioo

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
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

    private val runCampaignViewModel: RunCampaignViewModel by viewModels(ownerProducer = { this })
    private lateinit var locationListener: LocationListener
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var isCalculating = false // flag to check if distance calculation is active
    private var previousLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRunCampaignBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
        // set up location listener
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                if (isCalculating) { // only calculate distance if flag is true
                    if (previousLocation != null) {
                        runCampaignViewModel.calculateDistance(
                            previousLocation!!.latitude,
                            previousLocation!!.longitude,
                            location.latitude,
                            location.longitude
                        )
                    }
                    previousLocation = location
                }
            }

            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

        }

        // set up Start Calculation button click listener
        binding.btnStartTrip.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Snackbar.make(
                    requireView(),
                    getString(R.string.permission_needed_location),
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(getString(R.string.give_permission)) {
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }.show()
            } else {
                isCalculating = true // set flag to true
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000,
                    1f,
                    locationListener
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
            locationManager.removeUpdates(locationListener)
            binding.btnStartTrip.isEnabled = true // enable Start Calculation button
            binding.btnStartTrip.visibility = View.VISIBLE
            binding.btnStopTrip.isEnabled = false // disable button
            binding.btnStopTrip.visibility = View.GONE
            runCampaignViewModel.resetDistance() // reset total distance
        }
    }

    private fun registerLauncher() {
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        1000,
                        1f,
                        locationListener
                    )
                }
            } else {
                SnackbarHelper.showErrorSnackBar(
                    requireView(),
                    getString(R.string.permission_needed_location)
                )
            }
        }
    }

    private fun subscribeToObservers() {
        runCampaignViewModel.distanceDriven.observe(viewLifecycleOwner) { distanceDriven->
            if(distanceDriven == null){
                binding.tvKMDriven.text = "0"
            }
            val formattedDistance = String.format("%.2f", distanceDriven)
            binding.tvKMDriven.text = formattedDistance+getString(R.string.length_unit)
        }
    }
}