package com.stambulo.mobileshop.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.stambulo.mobileshop.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
    }
}