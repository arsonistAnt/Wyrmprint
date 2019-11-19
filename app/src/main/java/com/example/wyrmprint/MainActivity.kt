package com.example.wyrmprint

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Timber setup
        Timber.plant(Timber.DebugTree())

        // TODO: Permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE),
                0
            )
        }
        setContentView(R.layout.activity_main)
    }
}