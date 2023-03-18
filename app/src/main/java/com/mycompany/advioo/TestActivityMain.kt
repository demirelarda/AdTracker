package com.mycompany.advioo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mycompany.advioo.ui.fragments.AppFragmentFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TestActivityMain : AppCompatActivity() {

    @Inject
    lateinit var fragmentFactory: AppFragmentFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.fragmentFactory = fragmentFactory
        setContentView(R.layout.activity_test_main)


    }
}