package com.mycompany.advioo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mycompany.advioo.databinding.FragmentRunCampaignBinding
import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.mycompany.advioo.services.LocationTrackingService

private const val LOCATION_AND_NOTIFICATION_PERMISSION_REQUEST_CODE = 1

class RunCampaignFragment : Fragment() {

    private var _binding: FragmentRunCampaignBinding? = null
    private val binding get() = _binding!!




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRunCampaignBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSaveTrip.visibility = View.GONE
        binding.btnStopTrip.visibility = View.GONE




        binding.btnStartTrip.setOnClickListener {
            requestLocationAndNotificationPermissions()
        }

        binding.btnStopTrip.setOnClickListener {
            stopTracking()
            binding.tvKMDriven.text = "0.00 KM"
            binding.btnStartTrip.visibility = View.VISIBLE
            binding.btnStopTrip.visibility = View.GONE
            binding.btnSaveTrip.visibility = View.GONE
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
            val distanceDriven = intent?.getDoubleExtra("distanceDriven", 0.0)
            distanceDriven?.let {
                binding.tvKMDriven.text = String.format("%.2f KM", it)
            }
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



}
