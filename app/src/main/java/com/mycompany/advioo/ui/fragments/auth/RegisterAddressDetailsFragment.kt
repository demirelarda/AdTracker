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
import com.mycompany.advioo.util.Util.REGISTER_STRING
import com.mycompany.advioo.viewmodels.RegisterAddressDetailsViewModel
import com.mycompany.advioo.viewmodels.SharedRegisterViewModel


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
        if(bundle!=null){
            val args = RegisterAddressDetailsFragmentArgs.fromBundle(bundle)
            if(args.registeringUser == REGISTER_STRING){
                val state = args.selectedLocationArray?.get(0) ?: ""
                val city = args.selectedLocationArray?.get(1) ?: ""
                if(state!=""||city!=""){
                    binding.tvSelectCity.text = "$city, $state"
                    observeLocalDriver(true)
                }
                setupNormalViews()
            }
            else{
                setupEditUserView()
                isInEditMode = true
            }

        }
        else{
            setupEditUserView()
            isInEditMode = true
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
        observeLocalDriver(false)
        binding.tvTitleAddressDetails.text = getString(R.string.edit_address_information)
        binding.btnContinueUserWorkDetails.text = getString(R.string.save_changes)
        binding.tvSelectCity.setOnClickListener {
            val action = RegisterAddressDetailsFragmentDirections.actionRegisterAddressDetailsFragmentToStateListFragment()
            findNavController().navigate(action)
        }
    }

    private fun setupNormalViews(){

        //viewModel = ViewModelProvider(requireActivity()).get(SharedRegisterViewModel::class.java)
        binding.tfFullName.setText((sharedRegisterViewModel.driver.value?.firstName.toString() ?: "") +" "+ (sharedRegisterViewModel.driver.value?.lastName.toString() ?: ""))
        binding.tvSelectCity.setOnClickListener {
            val action = RegisterAddressDetailsFragmentDirections.actionRegisterAddressDetailsFragmentToStateListFragment()
            Navigation.findNavController(requireView()).navigate(action)
        }

        binding.btnContinueUserWorkDetails.setOnClickListener {
            val fullName = binding.tfFullName.text.toString().trim()
            val city= binding.tvSelectCity.text.toString().trim()
            val address1 = binding.tfAddressRow1.text.toString().trim()
            val address2 = binding.tfAddressRow2.text.toString().trim()
            val zipCode = binding.tfPostalCode.text.toString().trim()

            if (registerAddressDetailsViewModel.isInputDataValid(fullName,city,address1,zipCode)) {
                sharedRegisterViewModel.setAddressFullName(fullName)
                sharedRegisterViewModel.setCity(city)
                sharedRegisterViewModel.setAddressRow1(address1)
                if(address2.isNotEmpty()){
                    sharedRegisterViewModel.setAddressRow2(address2)
                }
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
            binding.tfFullName.setText(localDriver.name+" "+localDriver.surname)
            if(!editedCity){
                binding.tvSelectCity.text = localDriver.stateName+","+localDriver.cityName
            }
            binding.tfAddressRow1.setText(localDriver.addressRow1)
            binding.tfPostalCode.setText(localDriver.zipCode)
            if(localDriver.addressRow2 != ""){
                binding.tfAddressRow2.setText(localDriver.addressRow2)
            }
        }
    }




}