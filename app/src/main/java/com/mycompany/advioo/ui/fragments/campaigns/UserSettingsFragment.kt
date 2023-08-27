package com.mycompany.advioo.ui.fragments.campaigns

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.mycompany.advioo.R
import com.mycompany.advioo.databinding.FragmentUserSettingsBinding
import com.mycompany.advioo.ui.activities.MainActivity
import com.mycompany.advioo.ui.fragments.ContactUsFragment
import com.mycompany.advioo.ui.fragments.ReceivePaymentFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

//TODO: ADD A SYNC WITH SERVER OPTION

@AndroidEntryPoint
class UserSettingsFragment : Fragment() {

    private var _binding: FragmentUserSettingsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUserSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavView).visibility = View.VISIBLE
        setupViews()
        setupOnClickListeners()


    }


    private fun setupViews(){
        binding.tvUserName.text = firebaseAuth.currentUser?.displayName ?: ""
    }

    private fun setupOnClickListeners(){
        binding.tvBtnSignOut.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(),MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        binding.tvBtnEditPersonalInformation.setOnClickListener {
            val intent = Intent(requireContext(),MainActivity::class.java)
            intent.putExtra("personalInfo",true)
            intent.putExtra("editInfo",true)
            startActivity(intent)
        }

        binding.tvBtnEditDrivingInfo.setOnClickListener {
            val intent = Intent(requireContext(),MainActivity::class.java)
            intent.putExtra("drivingInfo",true)
            intent.putExtra("editInfo",true)
            startActivity(intent)
        }

        binding.tvBtnEditAddressInformation.setOnClickListener {
            val intent = Intent(requireContext(),MainActivity::class.java)
            intent.putExtra("addressInfo",true)
            intent.putExtra("editInfo",true)
            startActivity(intent)
        }

        binding.tvBtnContactUs.setOnClickListener {
            val fragment = ContactUsFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.mainFrameLayout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.tvBtnGetPayment.setOnClickListener {
            val fragment = ReceivePaymentFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.mainFrameLayout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }


    }


}