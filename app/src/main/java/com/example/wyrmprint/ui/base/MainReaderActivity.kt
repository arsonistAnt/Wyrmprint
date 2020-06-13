package com.example.wyrmprint.ui.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import androidx.navigation.ActivityNavigator
import androidx.navigation.navArgs
import com.example.wyrmprint.R
import com.example.wyrmprint.injection.InjectionProvider
import com.example.wyrmprint.injection.component.ActivityComponent
import com.example.wyrmprint.injection.component.DaggerActivityComponent
import com.example.wyrmprint.injection.module.ContextModule
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main_reader.*
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

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        constructBottomSheet()

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
     * Construct bottom sheet for the [MainReaderActivity]
     */
    private fun constructBottomSheet() {
        val bottomSheet = findViewById<View>(R.id.bottom_sheet_test)
        val bottomBehavior = BottomSheetBehavior.from(bottomSheet)
        // Get header of the bottom sheet and calculate its height.
        val header = bottomSheet.findViewById<View>(R.id.text_header)
        // Measure the height of the header to give to the peekHeight.
        header.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        main_reader_container?.setOnSystemUiVisibilityChangeListener { sysFlags ->
            when (sysFlags) {
                SystemUIState.UIVisible.ordinal -> {
                    bottomBehavior.isHideable = false
                    bottomBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
                else -> {
                    bottomBehavior.isHideable = true
                    bottomBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(bottomSheet) { v, insets ->
            bottomBehavior.peekHeight = header.measuredHeight + insets.systemWindowInsetBottom
            v.updatePadding(bottom = insets.systemWindowInsetBottom)
            insets
        }
        bottomBehavior.apply {
            isFitToContents = true
            isGestureInsetBottomIgnored = true
            peekHeight = header.measuredHeight
            skipCollapsed = false
            isHideable = true
            state = BottomSheetBehavior.STATE_HIDDEN
        }
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