package com.example.wyrmprint.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ActivityNavigator
import androidx.navigation.navArgs
import com.example.wyrmprint.R
import com.example.wyrmprint.injection.InjectionProvider
import com.example.wyrmprint.injection.component.ActivityComponent
import com.example.wyrmprint.injection.component.DaggerActivityComponent
import com.example.wyrmprint.injection.module.ContextModule
import me.zhanghai.android.systemuihelper.SystemUiHelper


class MainReaderActivity : AppCompatActivity(), InjectionProvider, UIVisibilityAction {
    override val component: ActivityComponent by lazy {
        val contextModule = ContextModule(this)
        DaggerActivityComponent.builder().contextModule(contextModule).build()
    }
    val safeArgs: MainReaderActivityArgs by navArgs()


    private var systemUiHelper: SystemUiHelper? = null

    enum class SystemUIState(stateNum: Int) {
            UIVisible(0),
        UIHidden(7)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.activity_main_reader)
        createSystemUiHelper()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // A workaround to allow animation transitions between Activities on the Navigation Component.
        ActivityNavigator.applyPopAnimationsToPendingTransition(this)
    }

    override fun show() {
        systemUiHelper?.show()
    }

    override fun hide() {
        systemUiHelper?.hide()
    }

    /**
     * Initialize the [SystemUiHelper] util object.
     */
    private fun createSystemUiHelper() {
        val level = SystemUiHelper.LEVEL_IMMERSIVE
        val flags = SystemUiHelper.FLAG_IMMERSIVE_STICKY or
                SystemUiHelper.FLAG_LAYOUT_IN_SCREEN_OLDER_DEVICES
        systemUiHelper = SystemUiHelper(this, level, flags)
    }
}

/**
 * Interface callback to show and hide system ui visibility from Fragments hosted on an Activity.
 */
interface UIVisibilityAction {
    fun show()
    fun hide()
}