package com.example.wyrmprint.ui.base

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.wyrmprint.R
import com.example.wyrmprint.databinding.ActivityMainBinding
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.community.material.CommunityMaterial
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private lateinit var mainBinding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timberSetup()
        permissionsCheck()
        bindingCreate()
        addBottomNavIcons(mainBinding.mainNavbarBottom.menu)
    }

    /**
     * Initialize data binding to view.
     */
    private fun bindingCreate() {
        mainBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )
    }

    /**
     * Permission checks for app.
     */
    private fun permissionsCheck() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE),
                0
            )
        }
    }

    /**
     * Setup menu item icons for the main bottom navigation view.
     *
     * @param menu the [Menu] object of the main bottom navigation view.
     */
    private fun addBottomNavIcons(menu: Menu?) =
        menu?.apply {
            val browseItem = findItem(R.id.item_browse)
            val recentItem = findItem(R.id.item_recent)
            val settingItem = findItem(R.id.item_setting)
            browseItem.icon = IconicsDrawable(applicationContext)
                .icon(CommunityMaterial.Icon.cmd_grid)
            recentItem.icon = IconicsDrawable(applicationContext)
                .icon(CommunityMaterial.Icon2.cmd_update)
            settingItem.icon = IconicsDrawable(applicationContext)
                .icon(CommunityMaterial.Icon.cmd_account_settings)
        }

    private fun timberSetup() = Timber.plant(Timber.DebugTree())
}