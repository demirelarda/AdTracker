package com.mycompany.advioo.ui.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.mycompany.advioo.R
import com.mycompany.advioo.databinding.ActivityMainPageBinding
import com.mycompany.advioo.models.User
import com.mycompany.advioo.ui.MainActivity
import java.text.SimpleDateFormat

class MainPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainPageBinding
    private lateinit var auth : FirebaseAuth
    private val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainPageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth

        getCurrentUserDetails()

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this,MainActivity::class.java)
            intent.putExtra("isLoggedOut",true)
            startActivity(intent)
            finish()
        }





    }

    fun getCurrentUserDetails(){
        db.collection("users")
            .document(auth.uid!!)
            .get()
            .addOnSuccessListener { document ->
                Log.i(javaClass.simpleName, document.toString())
                val user = document.toObject(User::class.java)!!
                populateUserDetails(user)
            }


    }

    private fun populateUserDetails(user: User){
        binding.email.text = "Email: ${user.email}"
        binding.username.text = "Username: ${user.email}"
        binding.userId.text = "User ID: ${user.id}"
        binding.phoneNumber.text = "Phone Number: ${user.phoneNumber}"
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = dateFormat.format(user.regDate.toDate())
        binding.regDate.text = "Registration Date: ${date}"

    }


}