package com.mycompany.advioo.ui.fragments.auth


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mycompany.advioo.R
import com.mycompany.advioo.databinding.FragmentRegisterUserWorkDetailsBinding
import com.mycompany.advioo.ui.activities.AppAdActivity
import com.mycompany.advioo.util.SnackbarHelper
import com.mycompany.advioo.util.Util
import com.mycompany.advioo.viewmodels.RegisterUserWorkDetailsViewModel
import com.mycompany.advioo.viewmodels.SharedRegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterUserWorkDetailsFragment : Fragment() {

    private var _binding: FragmentRegisterUserWorkDetailsBinding? = null
    private val binding get() = _binding!!
    private val sharedRegisterViewModel : SharedRegisterViewModel by activityViewModels()
    private val userWorkDetailsViewModel : RegisterUserWorkDetailsViewModel by viewModels(ownerProducer = { this } )
    private var isInEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterUserWorkDetailsBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        val bundle = arguments
        if(bundle != null){
            val args = RegisterFragmentArgs.fromBundle(bundle)
            if(args.registeringUser == Util.REGISTER_STRING){
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

        binding.ivBtnBackFromUserWorkDetails.setOnClickListener {
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


    }


    private fun setupEditUserView(){
        sharedRegisterViewModel.getLocalDriver()
        observeLocalDriver()
        binding.cboxTermsConditions.visibility = View.GONE
        binding.tvTermsConditionsText.visibility = View.GONE
        binding.tvTitleCarDetails.text = getString(R.string.edit_car_driving_info)
        binding.btnSignUp.text = getString(R.string.save_changes)

        binding.btnSignUp.setOnClickListener {
            val carBrand = binding.tfCarBrand.text.toString().trim()
            val carModel = binding.tfCarModel.text.toString().trim()
            val carYear = binding.tfCarYear.text.toString().trim()
            val carCondition = binding.carConditionDropDown.text.toString().trim()
            val avgKmDriven = binding.tfAvgKmDrivenPerMonth.text.toString().trim()
            var allowContact = false
            var rideShare = false
            allowContact = binding.cboxAllowMailPhone.isChecked
            rideShare = binding.cboxRideshare.isChecked
            println("car brand first = $carBrand")
            if(userWorkDetailsViewModel.isInputDataValid(carBrand = carBrand, carModel = carModel, carYear = carYear,carCondition=carCondition, avgKmDriven = avgKmDriven, termsConditions = binding.cboxTermsConditions, isUpdating = true)){
                println("car brand second = $carBrand")
                sharedRegisterViewModel.updateWorkDetails(carBrand,carModel,carYear,carCondition,avgKmDriven,rideShare,allowContact)
            }
            else{
                val errorMessage: String = resources.getString(userWorkDetailsViewModel.errorList[0])
                SnackbarHelper.showErrorSnackBar(requireView(),errorMessage)
            }
        }
    }

    private fun setupNormalViews(){
        subscribeToObservers()

        binding.btnSignUp.setOnClickListener {
            val carBrand = binding.tfCarBrand.text.toString().trim()
            val carModel = binding.tfCarModel.text.toString().trim()
            val carYear = binding.tfCarYear.text.toString().trim()
            val carCondition = binding.carConditionDropDown.text.toString().trim()
            val avgKmDriven = binding.tfAvgKmDrivenPerMonth.text.toString().trim()

            if (userWorkDetailsViewModel.isInputDataValid(carBrand = carBrand, carModel = carModel, carYear = carYear,carCondition=carCondition, avgKmDriven = avgKmDriven, termsConditions = binding.cboxTermsConditions, isUpdating = false)) {
                sharedRegisterViewModel.driver.value
                sharedRegisterViewModel.setCarBrand(carBrand)
                sharedRegisterViewModel.setCarModel(carModel)
                sharedRegisterViewModel.setCarCondition(carCondition)
                sharedRegisterViewModel.setCarYear(carYear)
                sharedRegisterViewModel.setAvgKmDriven(avgKmDriven)

                if(binding.cboxAllowMailPhone.isChecked){
                    sharedRegisterViewModel.setAllowContact(true)
                }
                else{
                    sharedRegisterViewModel.setAllowContact(false)
                }
                if(binding.cboxRideshare.isChecked){
                    sharedRegisterViewModel.setRideShare(true)
                }
                else{
                    sharedRegisterViewModel.setRideShare(false)
                }

                //register driver
                //navigate to home screen
                val email = sharedRegisterViewModel.driver.value?.email!!
                val password = sharedRegisterViewModel.password.value.toString()
                val userObject = sharedRegisterViewModel.driver.value!!
                userWorkDetailsViewModel.registerUser(email,password,userObject)


            } else {
                val errorMessage: String = resources.getString(userWorkDetailsViewModel.errorList.get(0)) //list contains all the errors, get the first error message.
                SnackbarHelper.showErrorSnackBar(requireView(),errorMessage)
            }
        }
    }




    override fun onResume() {
        super.onResume()
        val carConditions = resources.getStringArray(R.array.car_conditions)
        val arrayAdapter = ArrayAdapter(requireContext(),R.layout.dropdown_item,carConditions)
        binding.carConditionDropDown.setAdapter(arrayAdapter)
    }


    @SuppressLint("SetTextI18n")
    private fun observeLocalDriver(){
        sharedRegisterViewModel.localDriver.observe(viewLifecycleOwner){localDriver->
            binding.tfCarBrand.setText(localDriver.carBrand)
            binding.tfCarModel.setText(localDriver.carModel)
            binding.tfAvgKmDrivenPerMonth.setText(localDriver.avgKmDriven)
            binding.tfCarYear.setText(localDriver.carYear)
            binding.carConditionDropDown.setText(localDriver.carCondition)
            val carConditions = resources.getStringArray(R.array.car_conditions)
            val arrayAdapter = ArrayAdapter(requireContext(),R.layout.dropdown_item,carConditions)
            binding.carConditionDropDown.setAdapter(arrayAdapter)
            binding.carConditionDropDown.setText(localDriver.carCondition,false)
            if(localDriver.allowedContact){
                binding.cboxAllowMailPhone.isChecked = true
            }
            if(localDriver.rideShareDriver){
                binding.cboxRideshare.isChecked = true
            }
        }

        sharedRegisterViewModel.loadingState.observe(viewLifecycleOwner){loading->
            if(loading){
                binding.progressBarRegisterFinal.visibility = View.VISIBLE
                binding.btnSignUp.visibility = View.GONE
            }
            else{
                binding.progressBarRegisterFinal.visibility = View.GONE
                binding.btnSignUp.visibility = View.VISIBLE
            }
        }

        sharedRegisterViewModel.errorMessage.observe(viewLifecycleOwner){errorMessage->
            SnackbarHelper.showErrorSnackBar(requireView(),errorMessage)
        }

        sharedRegisterViewModel.successState.observe(viewLifecycleOwner){success->
            if(success){
                SnackbarHelper.showSuccessSnackBar(requireView(),getString(R.string.update_successful))
            }
        }
    }

    private fun subscribeToObservers() {
        userWorkDetailsViewModel.loadingState.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBarRegisterFinal.visibility = View.VISIBLE
            } else {
                binding.progressBarRegisterFinal.visibility = View.GONE
            }
        }

        userWorkDetailsViewModel.successState.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                SnackbarHelper.showSuccessSnackBar(requireView(),getString(R.string.register_success))
                val intent = Intent(requireContext(),AppAdActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }

        userWorkDetailsViewModel.failState.observe(viewLifecycleOwner) { isFail ->
            if (isFail) {
                SnackbarHelper.showErrorSnackBar(requireView(),getString(R.string.register_failed))
            }
        }

        userWorkDetailsViewModel.errorMessage.observe(viewLifecycleOwner){errorMessage->
            SnackbarHelper.showErrorSnackBar(requireView(),errorMessage)
        }

    }




}