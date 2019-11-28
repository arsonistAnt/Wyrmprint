package com.example.wyrmprint.injection.module

import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable

@Module
class DisposableModule {
    @Provides
    fun getCompositeDisposable() = CompositeDisposable()
}