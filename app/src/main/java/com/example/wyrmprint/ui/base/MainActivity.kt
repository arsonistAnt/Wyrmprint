package com.example.wyrmprint.ui.base

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.wyrmprint.R
import com.example.wyrmprint.databinding.ActivityMainBinding
import com.example.wyrmprint.injection.InjectionProvider
import com.example.wyrmprint.injection.component.ActivityComponent
import com.example.wyrmprint.injection.component.DaggerActivityComponent
import com.example.wyrmprint.injection.module.ContextModule
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.library.community.material.CommunityMaterial
import timber.log.Timber

class MainActivity : AppCompatActivity(), InjectionProvider {
    lateinit var mainBinding: ActivityMainBinding

    // Main component that provides dependencies to all Fragments.
    override val component: ActivityComponent by lazy {
        val contextModule = ContextModule(this)
        DaggerActivityComponent.builder().contextModule(contextModule).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timberSetup()
        permissionsCheck()
        bindingCreate()
        setupBottomNavView()
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
     * Configure bottom navigation view.
     */
    private fun setupBottomNavView() {
        val navController = findNavController(R.id.fragment_nav_component)
        addBottomNavIcons(mainBinding.mainNavbarBottom.menu)
        NavigationUI.setupWithNavController(mainBinding.mainNavbarBottom, navController)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            title = when(destination.id){
                R.id.favoriteFragment -> "Favorites"
                R.id.browseFragment -> "Browse"
                else -> supportActionBar?.title
            }
        }
    }

    /**
     * Setup menu item icons for the main bottom navigation view.
     *
     * @param menu the [Menu] object of the main bottom navigation view.
     * @see setupBottomNavView
     */
    private fun addBottomNavIcons(menu: Menu?) =
        menu?.apply {
            val browseItem = findItem(R.id.browseFragment)
            val favoriteItem = findItem(R.id.favoriteFragment)
            val settingItem = findItem(R.id.item_setting)
            browseItem.icon = IconicsDrawable(applicationContext)
                .icon(CommunityMaterial.Icon.cmd_grid)
            favoriteItem.icon = IconicsDrawable(applicationContext)
                .icon(CommunityMaterial.Icon2.cmd_heart)
            settingItem.icon = IconicsDrawable(applicationContext)
                .icon(CommunityMaterial.Icon.cmd_account_settings)
        }

    private fun timberSetup() = Timber.plant(Timber.DebugTree())
}