package com.mycompany.advioo.ui.fragments.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mycompany.advioo.R
import com.mycompany.advioo.databinding.FragmentRegisterUserWorkDetailsBinding
import com.mycompany.advioo.ui.activities.AppAdActivity


class RegisterUserWorkDetailsFragment : Fragment() {

    private var _binding: FragmentRegisterUserWorkDetailsBinding? = null
    private val binding get() = _binding!!


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

    override fun onResume() {
        super.onResume()
        binding.btnSignUp.setOnClickListener {
            val intent = Intent(requireContext(),AppAdActivity::class.java)
            startActivity(intent)
        }
    }


}