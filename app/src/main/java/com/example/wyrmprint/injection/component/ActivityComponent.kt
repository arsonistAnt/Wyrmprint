package com.example.wyrmprint.injection.component

import com.example.wyrmprint.injection.module.DragaliaServiceModule
import com.example.wyrmprint.ui.viewmodel.BrowserViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DragaliaServiceModule::class])
interface ActivityComponent {
    val browserViewModel: BrowserViewModel
}