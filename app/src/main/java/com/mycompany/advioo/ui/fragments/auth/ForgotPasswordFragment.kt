package com.mycompany.advioo.ui.fragments.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mycompany.advioo.R
import com.mycompany.advioo.databinding.FragmentForgotPasswordBinding
import com.mycompany.advioo.util.SnackbarHelper
import com.mycompany.advioo.viewmodels.ForgotPasswordViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    private lateinit var auth : FirebaseAuth

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!
    private val forgotPasswordViewModel : ForgotPasswordViewModel by viewModels(ownerProducer = { this } )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentForgotPasswordBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
        //return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
        binding.btnSendPasswordResetEmail.setOnClickListener {
            val email: String = binding.tfPassResetEmail.text.toString()
            if(forgotPasswordViewModel.isInputDataValid(email)){
                forgotPasswordViewModel.sendPasswordResetEmail(email)
            }
            else{
                val errorMessage : String = getString(forgotPasswordViewModel.errorList.get(0))
                SnackbarHelper.showErrorSnackBar(requireView(),errorMessage)
            }

        }

    }


    private fun subscribeToObservers() {
        forgotPasswordViewModel.loadingState.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBarForgotPassword.visibility = View.VISIBLE
            } else {
                binding.progressBarForgotPassword.visibility = View.GONE
            }
        }

        forgotPasswordViewModel.successState.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(requireContext(),getText(R.string.email_sent),Toast.LENGTH_LONG).show()
                val action = ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToLoginFragment()
                Navigation.findNavController(requireView()).navigate(action)
            }
        }

        forgotPasswordViewModel.failState.observe(viewLifecycleOwner) { isFail ->
            if (isFail) {
                SnackbarHelper.showErrorSnackBar(requireView(),getString(R.string.error_sent_password))
            }
        }

    }


}