package com.example.wyrmprint.injection.module

import android.content.Context
import androidx.room.Room
import com.example.wyrmprint.data.database.ComicDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ComicDatabaseModule {
    @Singleton
    @Provides
    fun comicDatabase(context: Context) = Room.databaseBuilder(
        context,
        ComicDatabase::class.java,
        "comic-database"
    ).build()

    @Singleton
    @Provides
    fun thumbnailDao(db: ComicDatabase) = db.thumbnailDao()
}