package com.example.wyrmprint.injection.module

import com.example.wyrmprint.data.remote.repository.ComicRepository
import com.example.wyrmprint.injection.component.DragaliaLifeModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DragaliaLifeModule::class])
interface ComicRepoModule {
    fun getComicRepo(): ComicRepository
}