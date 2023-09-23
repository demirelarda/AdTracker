package com.mycompany.advioo.ui.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.mycompany.advioo.R
import com.mycompany.advioo.adapters.CarPhotoSliderAdapter
import com.mycompany.advioo.databinding.FragmentShowPhotoBinding
import com.mycompany.advioo.models.CarImageDetails
import com.mycompany.advioo.models.campaignapplication.CampaignApplication
import com.mycompany.advioo.ui.activities.AppAdActivity
import com.mycompany.advioo.util.GlideApp
import com.mycompany.advioo.util.SnackbarHelper
import com.mycompany.advioo.viewmodels.ShowPhotoViewModel
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileNotFoundException

@AndroidEntryPoint
class ShowPhotoFragment : Fragment() {

    private var _binding: FragmentShowPhotoBinding? = null
    private val binding get() = _binding!!
    private lateinit var campaignApplication: CampaignApplication
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var cameraPermissionErrorCounter = 0
    private var photosToTakeList = mutableListOf<String>()
    private lateinit var carSliderAdapter: CarPhotoSliderAdapter
    private var hasCarPhotos: Boolean = false
    private val showPhotoViewModel : ShowPhotoViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    //TODO: SET "PHOTOS TO TAKE" TO A VIEWMODEL VARIABLE
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentShowPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerLauncher()
        subscribeToObservers()
        val bundle = arguments
        if(bundle != null){
            val args = ShowPhotoFragmentArgs.fromBundle(bundle)
            if(!args.takenPhotosList.isNullOrEmpty()){
                println("entered args")
                hasCarPhotos = true
                showPhotos(args.takenPhotosList.toList())
            }
        }

        if(requireActivity().intent.hasExtra("campaignApplication") && !hasCarPhotos){
            println("entered intent code")
            binding.llCarPhotos.visibility = View.GONE
            binding.btnUploadPhotos.visibility = View.GONE
            campaignApplication = requireActivity().intent.getParcelableExtra("campaignApplication")!!
            showPhotoViewModel.setCampaignApplication(campaignApplication)
            var photoPartsString = ""
            when(campaignApplication.selectedCampaignLevel){
                "Light"->{
                    photoPartsString = getString(R.string.odometer)+", "+getString(R.string.side_right_left)
                    photosToTakeList.add(getString(R.string.capture_odometer_photo))
                    photosToTakeList.add(getString(R.string.capture_side_photo))
                }
                "Advanced"->{
                    photoPartsString = getString(R.string.odometer)+", "+getString(R.string.side_right_left)
                    photosToTakeList.add(getString(R.string.capture_odometer_photo))
                    photosToTakeList.add(getString(R.string.capture_side_photo))
                }
                "Pro"->{
                    photoPartsString = getString(R.string.odometer)+", "+getString(R.string.side_right_left)+", "+getString(R.string.hood)
                    photosToTakeList.add(getString(R.string.capture_odometer_photo))
                    photosToTakeList.add(getString(R.string.capture_side_photo))
                    photosToTakeList.add(getString(R.string.capture_hood_photo))

                }
            }
            //showPhotoViewModel.setPhotosToTakeList(photosToTakeList)
            binding.tvPhotoParts.text = photoPartsString

            binding.btnTakePhotos.setOnClickListener {
                getPermissions()
            }

        }


    }


    private fun getPermissions(){
        val givePermissionString = getString(R.string.give_permission)
        val cameraRationaleString = getString(R.string.permission_needed_for_camera)
        if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.CAMERA)){
                //rationale
                Snackbar.make(requireView(),cameraRationaleString,Snackbar.LENGTH_INDEFINITE).setAction(givePermissionString,
                    View.OnClickListener {
                        //request permission
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }).show()
            }
            else{
                //request permission
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
        else{
            //go to camera fragment
            navigateToCameraFragment()
        }
    }

    private fun registerLauncher(){
        val cameraRationaleString = getString(R.string.permission_needed_for_camera)
        val cameraSettingsDeniedTwice = getString(R.string.you_have_denied_camera_permission_twice)

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){success->
            if(success){
                navigateToCameraFragment()
            }
            else{
                //permission denied
                cameraPermissionErrorCounter++
                if(cameraPermissionErrorCounter < 2){
                    SnackbarHelper.showErrorSnackBar(requireView(),cameraRationaleString)
                }
                else{
                    //TODO: CONSIDER ADDING INTENT TO GALLERY (USE ACTIVITY RESULT LAUNCHER)
                    //TODO: ALSO ADD THIS SITUATION TO SHARED PREF. TO CHECK IF THE USER DENIED TWICE
                    SnackbarHelper.showErrorSnackBar(requireView(),cameraSettingsDeniedTwice)
                }
            }
        }
    }

    private fun navigateToCameraFragment(){
        val action = ShowPhotoFragmentDirections.actionShowPhotoFragmentToTakePhotoFragment2(photosToTakeList.toTypedArray())
        Navigation.findNavController(requireView()).navigate(action)
    }

    private fun showPhotos(photoList: List<String>){
        println("show photos function ran")
        binding.btnTakePhotos.text = getString(R.string.take_photos_again)
        binding.btnUploadPhotos.visibility = View.VISIBLE
        binding.llCarPhotos.visibility = View.VISIBLE
        binding.llCarPhotoTexts.visibility = View.GONE
        val glide = GlideApp.with(this)
        carSliderAdapter = CarPhotoSliderAdapter(glide, photoList)
        binding.imageSliderCarPhotos.adapter = carSliderAdapter
        val dotsIndicator = requireActivity().findViewById<DotsIndicator>(R.id.spring_dots_indicator_user_car_photos)
        dotsIndicator.attachTo(binding.imageSliderCarPhotos)
        binding.btnTakePhotos.setOnClickListener {
            navigateToCameraFragment()
        }
        binding.btnUploadPhotos.setOnClickListener {
            val uriList: List<Uri> = photoList.map { filename ->
                val file = File(requireContext().filesDir, filename)
                Uri.fromFile(file)
            }

            val bitmapList = loadImagesFromStorage(uriList, requireContext())
            showPhotoViewModel.processCarImages(bitmapList)
        }
    }


    private fun loadImagesFromStorage(uris: List<Uri>,context: Context): List<Bitmap?> {
        return uris.map { uri ->
            try {
                context.contentResolver?.openInputStream(uri)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                null
            }
        }
    }

    private fun subscribeToObservers(){
        showPhotoViewModel.loadingState.observe(viewLifecycleOwner){loading->
            if(loading){
                binding.btnTakePhotos.visibility = View.GONE
                binding.btnUploadPhotos.visibility = View.GONE
                binding.progressBarShowPhoto.visibility = View.VISIBLE
            }
            else{
                binding.progressBarShowPhoto.visibility = View.GONE
            }
        }

        showPhotoViewModel.successState.observe(viewLifecycleOwner){success->
            if(success){
                Toast.makeText(requireContext(),getString(R.string.upload_photo_success),Toast.LENGTH_LONG).show()
                val intent = Intent(requireContext(),AppAdActivity::class.java)
                intent.putExtra("toMyCampaigns",true)
                requireContext().startActivity(intent)
                requireActivity().finish()
            }
        }

        showPhotoViewModel.failState.observe(viewLifecycleOwner){fail->
            if(fail){
                SnackbarHelper.showErrorSnackBar(requireView(),getString(R.string.an_error_occurred_network))
                binding.btnTakePhotos.visibility = View.VISIBLE
                binding.btnUploadPhotos.visibility = View.VISIBLE
            }
        }


    }

}



