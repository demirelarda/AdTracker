package com.mycompany.advioo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mycompany.advioo.databinding.FragmentRunCampaignBinding
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint

class RunCampaignFragment : Fragment() {

    private var _binding: FragmentRunCampaignBinding? = null
    private val binding get() = _binding!!
    private val accuracyList = mutableListOf<Pair<Float, GeoPoint>>()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private val locationPoints = mutableListOf<GeoPoint>()

    private var distanceDriven = 0f
    private var lastLocation: Location? = null
    private var startTime: Long = 0
    private var startPoint: GeoPoint? = null
    private val distancePoints = mutableListOf<Triple<GeoPoint, Float, Long>>()
    private lateinit var userEmail: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRunCampaignBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSaveTrip.visibility = View.GONE
        binding.btnStopTrip.visibility = View.GONE
        userEmail = FirebaseAuth.getInstance().currentUser?.email.toString()

        binding.btnSaveTrip.setOnClickListener {
            saveTripData()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val locationInterval = 1000L
        val locationFastestInterval = 500L
        val locationMaxWaitTime = 1000L


        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, locationInterval)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(locationFastestInterval)
            .setMaxUpdateDelayMillis(locationMaxWaitTime)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                for (location in locationResult.locations) {
                    if (lastLocation == null) {
                        lastLocation = location
                        startPoint = GeoPoint(location.latitude, location.longitude)
                        startTime = System.currentTimeMillis()
                    } else {
                        distanceDriven += lastLocation!!.distanceTo(location) / 1000
                        binding.tvKMDriven.text = String.format("%.2f KM", distanceDriven)
                        lastLocation = location
                    }

                    val currentTime = System.currentTimeMillis()

                    locationPoints.add(GeoPoint(location.latitude, location.longitude))
                    accuracyList.add(Pair(location.accuracy, GeoPoint(location.latitude, location.longitude)))
                    distancePoints.add(Triple(GeoPoint(location.latitude, location.longitude), distanceDriven, currentTime))
                }
            }
        }




        binding.btnStartTrip.setOnClickListener {
            startTracking()
            binding.btnStartTrip.visibility = View.GONE
            binding.btnStopTrip.visibility = View.VISIBLE
            binding.btnSaveTrip.visibility = View.VISIBLE
        }

        binding.btnStopTrip.setOnClickListener {
            stopTracking()
            distanceDriven = 0f
            lastLocation = null
            startTime = 0
            startPoint = null
            binding.tvKMDriven.text = "0.00 KM"
            binding.btnStartTrip.visibility = View.VISIBLE
            binding.btnStopTrip.visibility = View.GONE
            binding.btnSaveTrip.visibility = View.GONE
        }
    }

    private fun startTracking() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 100
            )
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private fun stopTracking() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startTracking()
            }
        }
    }

    private fun saveTripData() {
        if (lastLocation != null && startPoint != null) {
            val tripData = hashMapOf(
                "userEmail" to userEmail,
                "start_time" to startTime,
                "end_time" to System.currentTimeMillis(),
                "gps_accuracy" to lastLocation?.accuracy,
                "start_point" to startPoint,
                "end_point" to GeoPoint(lastLocation?.latitude ?: 0.0, lastLocation?.longitude ?: 0.0),
                "location_points" to locationPoints,
                "km_driven" to distanceDriven,
                "accuracy_list" to accuracyList,
                "distance_points" to distancePoints

            )

            binding.progressBarRunCampaign.visibility = View.VISIBLE

            FirebaseFirestore.getInstance().collection("location_data")
                .add(tripData)
                .addOnSuccessListener { documentReference ->
                    Log.d("RunCampaignFragment", "DocumentSnapshot written with ID: ${documentReference.id}")
                    binding.progressBarRunCampaign.visibility = View.GONE
                    Toast.makeText(requireContext(), "Trip data successfully saved.", Toast.LENGTH_LONG).show()
                    binding.btnSaveTrip.visibility = View.GONE
                    binding.btnStartTrip.visibility = View.VISIBLE
                }
                .addOnFailureListener { e ->
                    Log.w("RunCampaignFragment", "Error adding document", e)
                    binding.progressBarRunCampaign.visibility = View.GONE
                    Toast.makeText(requireContext(), "Error saving trip data.", Toast.LENGTH_LONG).show()
                }
        }
    }


    override fun onResume() {
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        stopTracking()
    }
}
