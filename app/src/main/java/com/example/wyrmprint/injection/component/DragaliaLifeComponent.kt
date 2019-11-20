package com.example.wyrmprint.injection.component

import com.example.wyrmprint.data.remote.DragaliaLifeApi
import com.example.wyrmprint.data.remote.DragaliaLifeService
import com.example.wyrmprint.util.DragaliaLifeUtil
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton


@Module
class DragaliaLifeModule {
    @Singleton
    @Provides
    fun provideDragaliaService(): DragaliaLifeService =
        Retrofit.Builder().baseUrl(DragaliaLifeUtil.baseUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(DragaliaLifeService::class.java)

    @Singleton
    @Provides
    fun provideDragaliaApi(dragaliaService: DragaliaLifeService) =
        DragaliaLifeApi(dragaliaService)
}