package com.mycompany.advioo.ui.fragments.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mycompany.advioo.R
import com.mycompany.advioo.databinding.FragmentForgotPasswordBinding
import com.mycompany.advioo.databinding.FragmentLoginBinding


class ForgotPasswordFragment : Fragment() {

    private lateinit var auth : FirebaseAuth

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

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
        binding.btnSendEmail.setOnClickListener {
            auth.sendPasswordResetEmail(binding.tfEmail.text.toString()).addOnSuccessListener {
                Toast.makeText(requireContext(),"An Email Has Been Sent To You, Please Check Your Spambox",Toast.LENGTH_LONG).show()
                val action = ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToLoginFragment(true)
                Navigation.findNavController(requireView()).navigate(action)
            }
        }

    }

}