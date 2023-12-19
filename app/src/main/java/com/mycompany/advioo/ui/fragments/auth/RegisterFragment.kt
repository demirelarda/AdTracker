package com.mycompany.advioo.ui.fragments.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.mycompany.advioo.R
import com.mycompany.advioo.databinding.FragmentRegisterBinding
import com.mycompany.advioo.ui.activities.AppAdActivity
import com.mycompany.advioo.util.SnackbarHelper
import com.mycompany.advioo.util.Util.REGISTER_STRING
import com.mycompany.advioo.viewmodels.RegisterViewModel
import com.mycompany.advioo.viewmodels.SharedRegisterViewModel


class RegisterFragment: Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val sharedRegisterViewModel : SharedRegisterViewModel by activityViewModels()
    private val registerViewModel : RegisterViewModel by viewModels(ownerProducer = { this } )
    private var isInEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_register, container, false)
        _binding = FragmentRegisterBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //sharedRegisterViewModel = ViewModelProvider(requireActivity()).get(SharedRegisterViewModel::class.java)

        val bundle = arguments
        if(bundle != null){
            val args = RegisterFragmentArgs.fromBundle(bundle)
            if(args.registeringUser == REGISTER_STRING){
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

        binding.ivBtnBackFromRegisterFirst.setOnClickListener {
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
        binding.btnContinueRegisterFirst.text = getString(R.string.save_changes)
        binding.tvSignUpTextRegister.text = getString(R.string.edit_personal_information)
        binding.tfPassword.visibility = View.GONE
        binding.tfPasswordAgain.visibility = View.GONE
        binding.tilPasswordRegister.visibility = View.GONE
        binding.tilPasswordAgainRegister.visibility = View.GONE
        binding.btnContinueRegisterFirst.setOnClickListener {
                println("button clicked")
                val firstName = binding.tfFirstName.text.toString().trim()
                val lastName = binding.tfLastName.text.toString().trim()
                val email = binding.tfEmailSignup.text.toString().trim()
                val phoneNumber = binding.tfPhoneNumberSignup.text.toString().trim()

                if (registerViewModel.isInputDataValid(firstName, lastName, email,"","",true,phoneNumber)) {
                    sharedRegisterViewModel.setFirstName(firstName)
                    sharedRegisterViewModel.setLastName(lastName)
                    sharedRegisterViewModel.setEmail(email)
                    sharedRegisterViewModel.updateUserPersonalDetails(email,firstName,lastName,phoneNumber)
                } else {
                    val errorMessage: String = resources.getString(registerViewModel.errorList[0]) //list contains all the errors, get the first error message.
                    SnackbarHelper.showErrorSnackBar(requireView(),errorMessage)
                }
        }
    }

    private fun setupNormalViews(){
        binding.btnContinueRegisterFirst.setOnClickListener {
            val firstName = binding.tfFirstName.text.toString().trim()
            val lastName = binding.tfLastName.text.toString().trim()
            val email = binding.tfEmailSignup.text.toString().trim()
            val password = binding.tfPassword.text.toString().trim()
            val passwordAgain = binding.tfPasswordAgain.text.toString().trim()
            val phoneNumber = binding.tfPhoneNumberSignup.text.toString().trim()

            if (registerViewModel.isInputDataValid(firstName, lastName, email, password, passwordAgain,false,phoneNumber)) {
                sharedRegisterViewModel.driver.value
                sharedRegisterViewModel.setFirstName(firstName)
                sharedRegisterViewModel.setLastName(lastName)
                sharedRegisterViewModel.setEmail(email)
                sharedRegisterViewModel.setPassword(password)
                sharedRegisterViewModel.setPhoneNumber(phoneNumber)
                val action = RegisterFragmentDirections.actionRegisterFragmentToRegisterAddressDetailsFragment(REGISTER_STRING)
                Navigation.findNavController(requireView()).navigate(action)
            } else {
                val errorMessage: String = resources.getString(registerViewModel.errorList[0]) //list contains all the errors, get the first error message.
                SnackbarHelper.showErrorSnackBar(requireView(),errorMessage)
            }
        }
    }


    private fun observeLocalDriver(){
        sharedRegisterViewModel.localDriver.observe(viewLifecycleOwner){localDriver->
            binding.tfFirstName.setText(localDriver.name)
            binding.tfLastName.setText(localDriver.surname)
            binding.tfEmailSignup.setText(localDriver.email)
            binding.tfPhoneNumberSignup.setText(localDriver.phoneNumber)
        }

        sharedRegisterViewModel.errorMessage.observe(viewLifecycleOwner){errorMessage->
            SnackbarHelper.showErrorSnackBar(requireView(),errorMessage)
        }

        sharedRegisterViewModel.successState.observe(viewLifecycleOwner){success->
            if(success){
                SnackbarHelper.showSuccessSnackBar(requireView(),getString(R.string.update_successful))
            }
        }
        sharedRegisterViewModel.loadingState.observe(viewLifecycleOwner){loading->
            if(loading){
                binding.llFormHolderRegister.visibility = View.GONE
                binding.progressBarRegister.visibility = View.VISIBLE
            }
            else{
                binding.llFormHolderRegister.visibility = View.VISIBLE
                binding.progressBarRegister.visibility = View.GONE
            }
        }


    }








}