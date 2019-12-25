package com.example.wyrmprint.injection.component

import com.example.wyrmprint.injection.module.ComicDatabaseModule
import com.example.wyrmprint.injection.module.ContextModule
import com.example.wyrmprint.injection.module.DisposableModule
import com.example.wyrmprint.injection.module.DragaliaServiceModule
import com.example.wyrmprint.ui.viewmodel.BrowserViewModel
import com.example.wyrmprint.ui.viewmodel.ComicPagerViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [DragaliaServiceModule::class,
        DisposableModule::class,
        ContextModule::class,
        ComicDatabaseModule::class]
)
interface ActivityComponent {
    val browserViewModel: BrowserViewModel
    val comicPagerViewModel: ComicPagerViewModel
}