package com.example.wyrmprint.data.model

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ThumbnailData::class], version = 1)
abstract class ComicDatabase : RoomDatabase() {
    abstract fun thumbnailDao(): ThumbnailDao
}