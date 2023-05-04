package com.mycompany.advioo.ui.fragments.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.mycompany.advioo.databinding.FragmentRegisterBinding
import com.mycompany.advioo.util.SnackbarHelper
import com.mycompany.advioo.viewmodels.RegisterViewModel
import com.mycompany.advioo.viewmodels.SharedRegisterViewModel


class RegisterFragment: Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val sharedRegisterViewModel : SharedRegisterViewModel by activityViewModels()
    private val registerViewModel : RegisterViewModel by viewModels(ownerProducer = { this } )

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
        binding.btnContinueRegisterFirst.setOnClickListener {
            val firstName = binding.tfFirstName.text.toString().trim()
            val lastName = binding.tfLastName.text.toString().trim()
            val email = binding.tfEmailSignup.text.toString().trim()
            val password = binding.tfPassword.text.toString().trim()
            val passwordAgain = binding.tfPasswordAgain.text.toString().trim()

            if (registerViewModel.isInputDataValid(firstName, lastName, email, password, passwordAgain)) {
                sharedRegisterViewModel.driver.value
                sharedRegisterViewModel.setFirstName(firstName)
                sharedRegisterViewModel.setLastName(lastName)
                sharedRegisterViewModel.setEmail(email)
                sharedRegisterViewModel.setPassword(password)
                val action = RegisterFragmentDirections.actionRegisterFragmentToRegisterAddressDetailsFragment()
                Navigation.findNavController(requireView()).navigate(action)
            } else {
                val errorMessage: String = resources.getString(registerViewModel.errorList.get(0)) //list contains all the errors, get the first error message.
                SnackbarHelper.showErrorSnackBar(requireView(),errorMessage)
            }
        }
    }








}