package com.mycompany.advioo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mycompany.advioo.R
import dagger.hilt.android.AndroidEntryPoint


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}