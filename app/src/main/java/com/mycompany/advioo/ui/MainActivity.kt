package com.mycompany.advioo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mycompany.advioo.R
import com.mycompany.advioo.ui.fragments.AppFragmentFactory
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
}