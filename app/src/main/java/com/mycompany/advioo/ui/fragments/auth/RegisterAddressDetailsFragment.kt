package com.mycompany.advioo.ui.fragments.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.mycompany.advioo.R
import com.mycompany.advioo.databinding.FragmentRegisterAddressDetailsBinding
import com.mycompany.advioo.ui.activities.AppAdActivity
import com.mycompany.advioo.util.SnackbarHelper
import com.mycompany.advioo.util.Util.EDIT_STRING
import com.mycompany.advioo.util.Util.REGISTER_STRING
import com.mycompany.advioo.viewmodels.RegisterAddressDetailsViewModel
import com.mycompany.advioo.viewmodels.SharedRegisterViewModel


//TODO: FIX THE SUCCESS BAR SHOWING UP BUG
//TODO: REFRESH THE VIEWMODEL VALUES AFTER EDITING IS DONE
//TODO: FOR REGISTER: ADD SPESIFIC ERRORS

class RegisterAddressDetailsFragment : Fragment() {

    private var _binding: FragmentRegisterAddressDetailsBinding? = null
    private val binding get() = _binding!!
    private val sharedRegisterViewModel : SharedRegisterViewModel by activityViewModels()
    private val registerAddressDetailsViewModel : RegisterAddressDetailsViewModel by viewModels(ownerProducer = { this } )
    private var isInEditMode = false
    private lateinit var userCity: String
    private lateinit var userState: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterAddressDetailsBinding.inflate(inflater,container,false)
        val view = binding.root
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        val args = RegisterAddressDetailsFragmentArgs.fromBundle(bundle!!)
        if(args.registeringUser == REGISTER_STRING){
            setupNormalViews()
            sharedRegisterViewModel.setEditMode(false)
            if(args.selectedLocationArray != null){
                val state = args.selectedLocationArray[0]
                val city = args.selectedLocationArray[1]
                binding.tvSelectCity.text = "$state, $city"
            }
        }
        else{
            if(args.registeringUser == EDIT_STRING){
                setupEditUserView()
                if(args.selectedLocationArray == null){
                    observeLocalDriver(false)
                }
                else{
                    observeLocalDriver(true)
                }
                sharedRegisterViewModel.setEditMode(true)
                if(args.selectedLocationArray != null){
                    observeLocalDriver(true)
                    val state = args.selectedLocationArray[0]
                    val city = args.selectedLocationArray[1]
                    binding.tvSelectCity.text = "$state, $city"
                }
            }
        }



        binding.ivBtnBackFromAddressDetails.setOnClickListener {
            if(isInEditMode){
                val intent = Intent(activity, AppAdActivity::class.java)
                intent.putExtra("toAccountSettings", true)
                startActivity(intent)
                requireActivity().finish()
            }
            else{
                findNavController().popBackStack()
            }
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isInEditMode) {
                    val intent = Intent(activity, AppAdActivity::class.java)
                    intent.putExtra("toAccountSettings", true)
                    startActivity(intent)
                    requireActivity().finish()
                } else {
                    findNavController().popBackStack()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)




    }


    private fun setupEditUserView(){
        sharedRegisterViewModel.getLocalDriver()
        binding.tvTitleAddressDetails.text = getString(R.string.edit_address_information)
        binding.btnContinueUserWorkDetails.text = getString(R.string.save_changes)
        binding.tvSelectCity.setOnClickListener {
            sharedRegisterViewModel.setEditMode(true)
            val action = RegisterAddressDetailsFragmentDirections.actionRegisterAddressDetailsFragmentToStateListFragment()
            findNavController().navigate(action)
        }
        binding.btnContinueUserWorkDetails.setOnClickListener {
            val city= binding.tvSelectCity.text.toString().trim()
            val zipCode = binding.tfPostalCode.text.toString().trim()
            if(registerAddressDetailsViewModel.isInputDataValid(city = city, zipCode = zipCode)){
                sharedRegisterViewModel.updateDriverAddressData(city,zipCode)
            }
        }
    }

    private fun setupNormalViews(){

        //viewModel = ViewModelProvider(requireActivity()).get(SharedRegisterViewModel::class.java)
        binding.tvSelectCity.setOnClickListener {
            sharedRegisterViewModel.setEditMode(false)
            val action = RegisterAddressDetailsFragmentDirections.actionRegisterAddressDetailsFragmentToStateListFragment()
            Navigation.findNavController(requireView()).navigate(action)
        }

        binding.btnContinueUserWorkDetails.setOnClickListener {
            val city= binding.tvSelectCity.text.toString().trim()
            val zipCode = binding.tfPostalCode.text.toString().trim()

            if (registerAddressDetailsViewModel.isInputDataValid(city,zipCode)) {
                sharedRegisterViewModel.setCity(city)
                sharedRegisterViewModel.setZipCode(zipCode)
                val action = RegisterAddressDetailsFragmentDirections.actionRegisterAddressDetailsFragmentToRegisterUserWorkDetailsFragment(REGISTER_STRING)
                Navigation.findNavController(requireView()).navigate(action)
            } else {
                val errorMessage: String = resources.getString(registerAddressDetailsViewModel.errorList[0]) //list contains all the errors, get the first error message.
                SnackbarHelper.showErrorSnackBar(requireView(),errorMessage)
            }

        }
    }




    @SuppressLint("SetTextI18n")
    private fun observeLocalDriver(editedCity: Boolean){
        sharedRegisterViewModel.localDriver.observe(viewLifecycleOwner){localDriver->
            if(!editedCity){
                binding.tvSelectCity.text = localDriver.stateName+","+localDriver.cityName
            }
            binding.tfPostalCode.setText(localDriver.zipCode)
        }

        sharedRegisterViewModel.successState.observe(viewLifecycleOwner){success->
            if(success){
                SnackbarHelper.showSuccessSnackBar(requireView(),getString(R.string.update_successful))
            }
        }
        sharedRegisterViewModel.loadingState.observe(viewLifecycleOwner){loading->
            if(loading){
                binding.progressBarAddressDetails.visibility = View.VISIBLE
                binding.btnContinueUserWorkDetails.visibility = View.GONE
            }
            else{
                binding.progressBarAddressDetails.visibility = View.GONE
                binding.btnContinueUserWorkDetails.visibility = View.VISIBLE
            }
        }
        sharedRegisterViewModel.errorMessage.observe(viewLifecycleOwner){errorMessage->
            SnackbarHelper.showErrorSnackBar(requireView(),errorMessage)
        }
    }




}