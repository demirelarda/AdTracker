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
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.mycompany.advioo.databinding.FragmentRegisterUserWorkDetailsBinding
import com.mycompany.advioo.databinding.FragmentRunCampaignBinding
import com.mycompany.advioo.util.SnackbarHelper
import com.mycompany.advioo.viewmodels.RegisterViewModel
import com.mycompany.advioo.viewmodels.RunCampaignViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RunCampaignFragment @Inject constructor(
    val locationManager: LocationManager
) : Fragment() {

    private val runCampaignViewModel : RunCampaignViewModel by viewModels(ownerProducer = { this } )
    private lateinit var locationListener: LocationListener
    private lateinit var permissionLauncher: ActivityResultLauncher<String>



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_run_campaign, container, false)
    }


    private var previousLocation: Location? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        locationListener = object : LocationListener{
            override fun onLocationChanged(location: Location) {
                println("location: $location")
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

        if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.ACCESS_FINE_LOCATION)){
                Snackbar.make(requireView(),getString(R.string.permission_needed_location),Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.give_permission)){
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }.show()
            }else{
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,1f,locationListener)
        }

    }

    private fun registerLauncher(){
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){result->
            if(result){
                if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,1f,locationListener)
                }
            }
            else{
                SnackbarHelper.showErrorSnackBar(requireView(),getString(R.string.permission_needed_location))
            }
        }
    }




}