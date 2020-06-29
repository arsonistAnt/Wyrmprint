package com.example.wyrmprint.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.wyrmprint.data.model.ThumbnailData
import com.example.wyrmprint.data.model.ThumbnailFavorite
import com.example.wyrmprint.data.model.ThumbnailUrl
import io.reactivex.Single

@Dao
interface ThumbnailCacheDao {
    @Query("SELECT * from thumbnail_data where pageNumber = :pageNum ORDER BY comicNumber DESC")
    fun getThumbnailPage(pageNum: Int): Single<List<ThumbnailData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertThumbnailData(thumbnailData: List<ThumbnailData>)

    @Query("DELETE FROM thumbnail_data")
    fun clearThumbnailData()

    @Update
    fun updateThumbnailData(thumbnailData: ThumbnailData)

    @Query("UPDATE thumbnail_data SET isFavorite = :isFavorite WHERE comicId IN (:comicIdList)")
    fun updateFavorites(comicIdList: List<Int>, isFavorite: Boolean)

    @Query("SELECT thumbnailLarge, thumbnailSmall FROM thumbnail_data WHERE comicId = :id")
    fun getThumbnailUrl(id: Int): ThumbnailUrl?
}

@Dao
interface ThumbnailFavoritesDao {
    @Query("SELECT * from thumbnail_favorites")
    fun getFavorites(): LiveData<List<ThumbnailFavorite>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavorites(thumbnailData: List<ThumbnailFavorite>)

    @Delete
    fun deleteFavoriteRecords(favoriteComics: List<ThumbnailFavorite>)

    @Query("DELETE FROM thumbnail_favorites")
    fun clearFavoritesData()

    @Query("DELETE FROM thumbnail_favorites where comicId IN (:favoriteIdList)")
    fun deleteById(favoriteIdList: List<Int>)

    @Query("SELECT COUNT() FROM thumbnail_favorites WHERE comicId = :id")
    fun count(id: Int): Int
}

@Database(entities = [ThumbnailData::class, ThumbnailFavorite::class], version = 1)
abstract class ComicDatabase : RoomDatabase() {
    abstract fun thumbnailDao(): ThumbnailCacheDao
    abstract fun favoritesDao(): ThumbnailFavoritesDao
}