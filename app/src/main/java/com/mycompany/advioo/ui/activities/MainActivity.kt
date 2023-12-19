package com.mycompany.advioo.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.mycompany.advioo.R
import com.mycompany.advioo.ui.fragments.AppFragmentFactory
import com.mycompany.advioo.util.Util.EDIT_STRING
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var fragmentFactory: AppFragmentFactory
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.fragmentFactory = fragmentFactory
        setContentView(R.layout.activity_main)



    }




    private fun intentHandler(intent: Intent) {
        val navController = findNavController(R.id.fragmentContainerView)
        val bundle = Bundle()
        when {
            intent.getBooleanExtra("personalInfo", false) -> {
                bundle.putString("registeringUser",EDIT_STRING)
                navController.navigate(R.id.registerFragment, bundle)
            }
            intent.getBooleanExtra("drivingInfo", false) -> {
                bundle.putString("registeringUser",EDIT_STRING)
                navController.navigate(R.id.registerUserWorkDetailsFragment, bundle)
            }
            intent.getBooleanExtra("addressInfo", false) -> {
                bundle.putString("registeringUser",EDIT_STRING)
                navController.navigate(R.id.registerAddressDetailsFragment, bundle)
            }
            else -> {
               println("default")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        intentHandler(intent)
    }




    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            intentHandler(it)
        }
    }

}