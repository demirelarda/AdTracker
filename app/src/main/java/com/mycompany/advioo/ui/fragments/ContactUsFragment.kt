package com.mycompany.advioo.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.mycompany.advioo.R
import com.mycompany.advioo.databinding.FragmentContactUsBinding
import com.mycompany.advioo.util.SnackbarHelper
import com.mycompany.advioo.viewmodels.ContactUsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContactUsFragment : Fragment() {


    private var _binding: FragmentContactUsBinding? = null
    private val binding get() = _binding!!
    private val contactUsViewModel : ContactUsViewModel by viewModels(ownerProducer = { this } )
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentContactUsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupOnClickListeners()
        subscribeToObservers()
    }

    private fun setupViews(){
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavView).visibility = View.GONE
        binding.tvResponseEmail.text = firebaseAuth.currentUser!!.email
    }

    private fun setupOnClickListeners(){
        binding.btnSubmitContactMessage.setOnClickListener {
            contactUsViewModel.sendMessage(binding.tfMessageTitle.text.toString(),binding.tfMessageContent.text.toString())
        }

        binding.ivBtnBackFromContactUs.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun subscribeToObservers(){
        contactUsViewModel.loadingState.observe(viewLifecycleOwner){loading->
            if(loading){
                binding.scrollViewContactUs.visibility = View.GONE
                binding.progressBarContactUs.visibility = View.VISIBLE
            }
        }

        contactUsViewModel.failState.observe(viewLifecycleOwner){fail->
            if(fail){
                binding.progressBarContactUs.visibility = View.GONE
                binding.scrollViewContactUs.visibility = View.VISIBLE
            }
        }

        contactUsViewModel.successState.observe(viewLifecycleOwner){success->
            if(success){
                binding.progressBarContactUs.visibility = View.GONE
                binding.scrollViewContactUs.visibility = View.VISIBLE
                SnackbarHelper.showSuccessSnackBar(requireView(),getString(R.string.your_message_has_been_successfully_sent))
                binding.tfMessageTitle.setText("")
                binding.tfMessageContent.setText("")
            }
        }

        contactUsViewModel.errorMessage.observe(viewLifecycleOwner){
            SnackbarHelper.showErrorSnackBar(requireView(),it.toString())
        }
    }


}