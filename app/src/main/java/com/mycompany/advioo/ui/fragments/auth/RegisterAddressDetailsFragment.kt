package com.mycompany.advioo.ui.fragments.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.mycompany.advioo.R
import com.mycompany.advioo.databinding.FragmentLoginBinding
import com.mycompany.advioo.databinding.FragmentRegisterAddressDetailsBinding
import com.mycompany.advioo.ui.fragments.auth.city.CityListFragmentArgs


class RegisterAddressDetailsFragment : Fragment() {

    private var _binding: FragmentRegisterAddressDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var userCity: String
    private lateinit var userState: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterAddressDetailsBinding.inflate(inflater,container,false)
        val view = binding.root
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvSelectCity.setOnClickListener {
            val action = RegisterAddressDetailsFragmentDirections.actionRegisterAddressDetailsFragmentToStateListFragment()
            Navigation.findNavController(requireView()).navigate(action)
        }
        val bundle = arguments
        bundle?.let {
            val args = RegisterAddressDetailsFragmentArgs.fromBundle(it)
            val state = args.selectedLocationArray?.get(0) ?: ""
            val city = args.selectedLocationArray?.get(1) ?: ""
            if(state!=""||city!=""){
                binding.tvSelectCity.text = "$city, $state"
            }
        }
    }




}