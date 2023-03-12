package com.mycompany.advioo.ui.fragments.auth

import android.content.Intent
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
import com.mycompany.advioo.databinding.FragmentLoginBinding
import com.mycompany.advioo.databinding.FragmentStartingBinding
import com.mycompany.advioo.other.BaseFragment
import com.mycompany.advioo.ui.activities.MainPageActivity


class LoginFragment : BaseFragment() {

    private lateinit var auth : FirebaseAuth

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        var isAfterRegister : Boolean = false

        val bundle = arguments
        if(bundle != null){
            val args = LoginFragmentArgs.fromBundle(bundle)
            isAfterRegister = args.isAfterRegister
        }

        if(!isAfterRegister){
            val intent = Intent(requireContext(),MainPageActivity::class.java)
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
        /*
        binding.btnLoginAuth.setOnClickListener {
            loginUser()
        }
        binding.tvGoToRegisterFromLogin.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            Navigation.findNavController(it).navigate(action)
        }
        binding.tvForgotPassword.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment()
            Navigation.findNavController(it).navigate(action)
        }

         */
    }

    fun loginUser(){

        val email = binding.tfEmailLogin.text.toString()
        val password = binding.tfPasswordLogin.text.toString()

        if(email != "" && password!=""){
            showProgressBar("Please Wait...")
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener{ task->

                if(task.isSuccessful){
                    hideProgressBar()
                    val intent = Intent(requireContext(),MainPageActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()

                }
                else{
                    hideProgressBar()
                    Toast.makeText(requireContext(),"Login failed, try again!",Toast.LENGTH_LONG).show()
                }
            }
        }
        else{
            hideProgressBar()
            Toast.makeText(requireContext(),"Please Don't Leave The Entries Blank", Toast.LENGTH_LONG).show()
        }

    }


}