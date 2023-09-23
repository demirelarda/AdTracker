package com.mycompany.advioo.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mycompany.advioo.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PhotoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)
    }
}