package com.example.wyrmprint.injection.module

import com.example.wyrmprint.data.remote.DragaliaLifeApi
import com.example.wyrmprint.injection.component.DragaliaLifeModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DragaliaLifeModule::class])
interface DragaliaLifeComponent {
    fun getApi(): DragaliaLifeApi
}