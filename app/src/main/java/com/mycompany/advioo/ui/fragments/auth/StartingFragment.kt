package com.mycompany.advioo.ui.fragments.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mycompany.advioo.R
import com.mycompany.advioo.databinding.FragmentStartingBinding
import com.mycompany.advioo.ui.activities.MainPageActivity


class StartingFragment : Fragment() {

    private var _binding: FragmentStartingBinding? = null

    private val binding get() = _binding!!

    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        var isLoggedOut = false

        if(requireActivity().intent.hasExtra("isLoggedOut")){
            isLoggedOut = requireActivity().intent.getBooleanExtra("isLoggedOut",false)
            println(isLoggedOut)
        }

        if(!isLoggedOut){
            if(auth.currentUser!=null){
                println("auth pass")
                val intent = Intent(requireContext(), MainPageActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_starting, container, false)
        _binding = FragmentStartingBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnGoToLoginFromStart.setOnClickListener {
            val action = StartingFragmentDirections.actionStartingFragmentToLoginFragment(true)
            Navigation.findNavController(it).navigate(action)
        }

        binding.btnGoToRegisterFromStart.setOnClickListener{
            val action = StartingFragmentDirections.actionStartingFragmentToRegisterFragment()
            Navigation.findNavController(it).navigate(action)
        }

    }


}