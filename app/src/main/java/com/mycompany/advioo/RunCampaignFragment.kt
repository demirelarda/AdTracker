package com.mycompany.advioo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mycompany.advioo.databinding.FragmentRunCampaignBinding
import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.preference.PreferenceManager
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mycompany.advioo.services.LocationTrackingService
import com.mycompany.advioo.services.TripData
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline

private const val LOCATION_AND_NOTIFICATION_PERMISSION_REQUEST_CODE = 1
private const val BATTERY_OPTIMIZATION_REQUEST_CODE = 2
private lateinit var mapView: MapView
private lateinit var polyline: Polyline



class RunCampaignFragment : Fragment() {

    private var _binding: FragmentRunCampaignBinding? = null
    private val binding get() = _binding!!
    private lateinit var tripData: TripData
    val userEmail = FirebaseAuth.getInstance().currentUser?.email

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRunCampaignBinding.inflate(inflater, container, false)
        mapView = binding.mapView
        mapView.setTileSource(TileSourceFactory.MAPNIK)

        val osmConfig = org.osmdroid.config.Configuration.getInstance()
        osmConfig.userAgentValue = "${BuildConfig.APPLICATION_ID}/${BuildConfig.VERSION_NAME}"
        osmConfig.load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()))
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSaveTrip.visibility = View.GONE
        binding.btnStopTrip.visibility = View.GONE

        polyline = Polyline()
        polyline.color = Color.RED
        polyline.width = 8f
        mapView.overlayManager.add(polyline)

        binding.btnStartTrip.setOnClickListener {
            requestLocationAndNotificationPermissions()
            checkBatteryOptimizationPermission()
        }

        binding.btnStopTrip.setOnClickListener {
            stopTracking()
            binding.tvKMDriven.text = "0.00 KM"
            binding.btnStartTrip.visibility = View.VISIBLE
            binding.btnStopTrip.visibility = View.GONE
            binding.btnSaveTrip.visibility = View.GONE
        }

        binding.btnSaveTrip.setOnClickListener {
            println("trip data = "+tripData.locationPoints)
            tripData.endTime = System.currentTimeMillis()
            tripData.userEmail = userEmail
            //println(tripData)
            saveTripDataToFirestore(tripData)
        }
    }

    private fun saveTripDataToFirestore(tripData: TripData) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseFirestore.getInstance()

        if (userId != null) {
            // Show ProgressBar
            binding.progressBarRunCampaign.visibility = View.VISIBLE

            db.collection("location_data")
                .add(tripData)
                .addOnSuccessListener {
                    // Hide ProgressBar
                    binding.progressBarRunCampaign.visibility = View.GONE

                    Toast.makeText(requireContext(), "Trip data saved successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    // Hide ProgressBar
                    binding.progressBarRunCampaign.visibility = View.GONE

                    Toast.makeText(requireContext(), "Error saving trip data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }




    private fun startTracking() {
        val startIntent = Intent(requireContext(), LocationTrackingService::class.java).apply {
            action = LocationTrackingService.ACTION_START
        }
        ContextCompat.startForegroundService(requireContext(), startIntent)

        binding.btnStartTrip.visibility = View.GONE
        binding.btnStopTrip.visibility = View.VISIBLE
        binding.btnSaveTrip.visibility = View.VISIBLE
    }

    private val locationUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            tripData = intent?.getParcelableExtra("tripData") ?: TripData()
            binding.tvKMDriven.text = String.format("%.2f KM", tripData.distanceDriven)
        }
    }

    private fun stopTracking() {
        val stopIntent = Intent(requireContext(), LocationTrackingService::class.java).apply {
            action = LocationTrackingService.ACTION_STOP
        }
        requireContext().stopService(stopIntent)

        binding.tvKMDriven.text = "0.00 KM"
        binding.btnStartTrip.visibility = View.VISIBLE
        binding.btnStopTrip.visibility = View.GONE
        binding.btnSaveTrip.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            locationUpdateReceiver, IntentFilter(LocationTrackingService.ACTION_LOCATION_UPDATE)
        )
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(locationUpdateReceiver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }


    private fun requestLocationAndNotificationPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        val notGrantedPermissions = permissions.filter { permission ->
            ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED
        }

        if (notGrantedPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(requireActivity(), notGrantedPermissions.toTypedArray(), LOCATION_AND_NOTIFICATION_PERMISSION_REQUEST_CODE)
        } else {
            startTracking()
        }
    }

    private fun showRationaleDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Permissions Required")
            .setMessage(message)
            .setPositiveButton("OK") { _, _ ->
                requestLocationAndNotificationPermissions()
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_AND_NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                startTracking()
            } else {
                val deniedPermissions = permissions.zip(grantResults.toList())
                    .filter { (_, result) -> result != PackageManager.PERMISSION_GRANTED }
                    .map { (permission, _) -> permission }

                val anyRationaleNeeded = deniedPermissions.any { permission ->
                    ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permission)
                }

                if (anyRationaleNeeded) {
                    when {
                        deniedPermissions.containsAll(listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) -> showRationaleDialog("This app requires location permissions to provide its services. Please grant the required permissions.")
                        deniedPermissions.contains(Manifest.permission.POST_NOTIFICATIONS) -> showRationaleDialog("This app requires notification permissions to provide its services. Please grant the required permissions.")
                        else -> showRationaleDialog("This app requires location and notification permissions to provide its services. Please grant the required permissions.")
                    }
                } else {
                    when {
                        deniedPermissions.containsAll(listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)) -> showPermissionDeniedDialog("Location permissions are required for the app to function properly. Since you have permanently denied the permissions, you need to enable them manually from the app settings.")
                        deniedPermissions.contains(Manifest.permission.POST_NOTIFICATIONS) -> showPermissionDeniedDialog("Notification permissions are required for the app to function properly. Since you have permanently denied the permissions, you need to enable them manually from the app settings.")
                        else -> showPermissionDeniedDialog("Location and notification permissions are required for the app to function properly. Since you have permanently denied the permissions, you need to enable them manually from the app settings.")
                    }
                }
            }
        }
    }

    private fun showPermissionDeniedDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Permissions Denied")
            .setMessage(message)
            .setPositiveButton("Go to Settings") { _, _ ->
                startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", requireActivity().packageName, null)
                })
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun requestBatteryOptimizationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = requireActivity().getSystemService(Context.POWER_SERVICE) as PowerManager
            val packageName = requireActivity().packageName
            if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivityForResult(intent, BATTERY_OPTIMIZATION_REQUEST_CODE)
            } else {
                startTracking()
            }
        } else {
            startTracking()
        }
    }

    private fun checkBatteryOptimizationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = requireActivity().getSystemService(Context.POWER_SERVICE) as PowerManager
            val packageName = requireActivity().packageName
            if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                binding.btnStartTrip.isEnabled = false
                requestBatteryOptimizationPermission()
            } else {
                binding.btnStartTrip.isEnabled = true
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BATTERY_OPTIMIZATION_REQUEST_CODE) {
            checkBatteryOptimizationPermission()
        }
    }





}