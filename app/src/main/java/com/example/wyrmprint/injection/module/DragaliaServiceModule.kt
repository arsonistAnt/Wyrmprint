package com.example.wyrmprint.injection.module

import android.os.Build
import com.example.wyrmprint.data.remote.DragaliaLifeService
import com.example.wyrmprint.util.DragaliaLifeUtil
import com.example.wyrmprint.util.TLSSocketFactory
import dagger.Module
import dagger.Provides
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Protocol
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
class DragaliaServiceModule {
    @Singleton
    @Provides
    fun provideDragaliaService(): DragaliaLifeService {
        val client = OkHttpClient.Builder().connectionPool(ConnectionPool(0, 5, TimeUnit.MINUTES))
            .protocols(listOf(Protocol.HTTP_1_1))
        // Re-route socket to activate TLS 1.2 and 1.1 for API lvl 19 and below.
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            val tlsSocketFactory = TLSSocketFactory()
            client.sslSocketFactory(tlsSocketFactory, tlsSocketFactory.getTrustManager())
        }

        return Retrofit.Builder().baseUrl(DragaliaLifeUtil.baseUrl)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client.build())
            .build()
            .create(DragaliaLifeService::class.java)
    }
}