package com.example.wyrmprint.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ActivityNavigator
import androidx.navigation.navArgs
import com.example.wyrmprint.injection.InjectionProvider
import com.example.wyrmprint.injection.component.ActivityComponent
import com.example.wyrmprint.injection.component.DaggerActivityComponent


class MainReaderActivity : AppCompatActivity(), InjectionProvider {
    override val component: ActivityComponent by lazy { DaggerActivityComponent.create() }
    val safeArgs: MainReaderActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // A workaround to allow animation transitions between Activities on the Navigation Component.
        ActivityNavigator.applyPopAnimationsToPendingTransition(this)
    }
}