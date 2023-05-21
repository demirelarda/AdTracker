package com.mycompany.advioo.ui.fragments.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mycompany.advioo.R
import com.mycompany.advioo.databinding.FragmentLoginBinding
import com.mycompany.advioo.ui.activities.AppAdActivity
import com.mycompany.advioo.util.SnackbarHelper
import com.mycompany.advioo.util.Util.REGISTER_STRING
import com.mycompany.advioo.viewmodels.LoginViewModel
import com.mycompany.advioo.viewmodels.RegisterUserWorkDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel : LoginViewModel by viewModels(ownerProducer = { this } )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(loginViewModel.isUserAlreadyLoggedIn() && !(requireActivity().intent.hasExtra("editInfo"))){
            val intent = Intent(requireContext(),AppAdActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_login, container, false)
        _binding = FragmentLoginBinding.inflate(inflater,container,false)
        val view = binding.root
        return view

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
        binding.tvForgotPassword.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment()
            Navigation.findNavController(requireView()).navigate(action)
        }
        binding.tvSignUp.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment(REGISTER_STRING)
            findNavController().navigate(action)
        }

        binding.btnLogin.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser(){

        val email = binding.tfEmailLogin.text.toString()
        val password = binding.tfPasswordLogin.text.toString()

        if(loginViewModel.isInputDataValid(email,password)){
            loginViewModel.loginUser(email,password)
        }
        else{
            SnackbarHelper.showErrorSnackBar(requireView(),getString(R.string.error_entries_blank))
        }
    }



    private fun subscribeToObservers() {
        loginViewModel.loadingState.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBarLogin.visibility = View.VISIBLE
            } else {
                binding.progressBarLogin.visibility = View.GONE
            }
        }

        loginViewModel.successState.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                //Toast.makeText(requireContext(),(getString(R.string.login_success)),Toast.LENGTH_LONG).show()
                val intent = Intent(requireContext(),AppAdActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }

        loginViewModel.failState.observe(viewLifecycleOwner) { isFail ->
            if (isFail) {
                SnackbarHelper.showErrorSnackBar(requireView(),getString(R.string.login_failed))
            }
        }



    }


}