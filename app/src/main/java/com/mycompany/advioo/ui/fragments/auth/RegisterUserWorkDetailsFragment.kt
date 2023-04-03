package com.mycompany.advioo.ui.fragments.auth


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.mycompany.advioo.R
import com.mycompany.advioo.databinding.FragmentRegisterUserWorkDetailsBinding
import com.mycompany.advioo.models.user.User
import com.mycompany.advioo.ui.activities.AppAdActivity
import com.mycompany.advioo.util.SnackbarHelper
import com.mycompany.advioo.viewmodels.RegisterUserWorkDetailsViewModel
import com.mycompany.advioo.viewmodels.SharedRegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterUserWorkDetailsFragment : Fragment() {

    private var _binding: FragmentRegisterUserWorkDetailsBinding? = null
    private val binding get() = _binding!!
    private val sharedRegisterViewModel : SharedRegisterViewModel by activityViewModels()
    private val userWorkDetailsViewModel : RegisterUserWorkDetailsViewModel by viewModels(ownerProducer = { this } )

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

        subscribeToObservers()

        binding.btnSignUp.setOnClickListener {
            val carBrand = binding.tfCarBrand.text.toString().trim()
            val carModel = binding.tfCarModel.text.toString().trim()
            val carYear = binding.tfCarYear.text.toString().trim()
            val carCondition = binding.carConditionDropDown.text.toString().trim()
            val licensePlate = binding.tfLicensePlate.text.toString().trim()
            val avgKmDriven = binding.tfAvgKmDrivenPerMonth.text.toString().trim()
            val workCity = binding.tfWorkCity.text.toString().trim()

            if (userWorkDetailsViewModel.isInputDataValid(carBrand = carBrand, carModel = carModel, carYear = carYear,carCondition=carCondition, licensePlate = licensePlate, avgKmDriven = avgKmDriven, workCity = workCity, termsConditions = binding.cboxTermsConditions)) {
                sharedRegisterViewModel.user.value
                sharedRegisterViewModel.setCarBrand(carBrand)
                sharedRegisterViewModel.setCarModel(carModel)
                sharedRegisterViewModel.setCarCondition(carCondition)
                sharedRegisterViewModel.setCarYear(carYear)
                sharedRegisterViewModel.setAvgKmDriven(avgKmDriven)
                sharedRegisterViewModel.setWorkCity(workCity)
                sharedRegisterViewModel.setLicensePlate(licensePlate)

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

                //register user
                //navigate to home screen
                val email = sharedRegisterViewModel.user.value?.email!!
                val password = sharedRegisterViewModel.password.value.toString()
                val userObject = sharedRegisterViewModel.user.value!!
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

    }




}