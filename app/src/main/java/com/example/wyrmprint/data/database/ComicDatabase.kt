package com.example.wyrmprint.data .database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.example.wyrmprint.data.model.ThumbnailData
import com.example.wyrmprint.data.model.ThumbnailFavorite
import io.reactivex.Single

@Dao
interface ThumbnailCacheDao {
    @Query("SELECT * from thumbnail_data where pageNumber = :pageNum ORDER BY comicNumber DESC")
    fun getThumbnailPage(pageNum: Int): Single<List<ThumbnailData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertThumbnailData(thumbnailData: List<ThumbnailData>)

    @Query("DELETE FROM thumbnail_data")
    fun clearThumbnailData()
}

@Dao
interface ThumbnailFavoritesDao {
    @Query("SELECT * from thumbnail_favorites")
    fun getFavorites(): LiveData<List<ThumbnailFavorite>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavorites(thumbnailData: List<ThumbnailFavorite>)

    @Query("DELETE FROM thumbnail_favorites")
    fun clearThumbnailData()
}

@Database(entities = [ThumbnailData::class, ThumbnailFavorite::class], version = 1)
abstract class ComicDatabase : RoomDatabase() {
    abstract fun thumbnailDao(): ThumbnailCacheDao
    abstract fun favoritesDao(): ThumbnailFavoritesDao
}