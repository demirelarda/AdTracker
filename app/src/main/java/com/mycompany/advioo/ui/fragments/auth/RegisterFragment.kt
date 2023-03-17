package com.mycompany.advioo.ui.fragments.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.mycompany.advioo.databinding.FragmentRegisterBinding
import com.mycompany.advioo.models.user.User
import com.mycompany.advioo.other.BaseFragment


class RegisterFragment : BaseFragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()

    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

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



    }

    fun registerUser(userInfo: User) {
        db.collection("users")
            .document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                println("success")
                Toast.makeText(requireContext(),"Please Login",Toast.LENGTH_LONG).show()
                val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment(true)
                Navigation.findNavController(requireView()).navigate(action)
                hideProgressBar()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(),"An Error Occurred, Please Try Again",Toast.LENGTH_LONG).show()
                hideProgressBar()
            }
    }



}