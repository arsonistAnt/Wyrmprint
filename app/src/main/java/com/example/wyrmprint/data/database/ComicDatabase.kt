package com.example.wyrmprint.data.database

import androidx.room.*
import com.example.wyrmprint.data.model.ThumbnailData
import io.reactivex.Single

@Dao
interface ThumbnailDao {
    @Query("SELECT * from thumbnail_data where pageNumber = :pageNum ORDER BY comicNumber DESC")
    fun getThumbnailPage(pageNum: Int): Single<List<ThumbnailData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertThumbnailData(thumbnailData: List<ThumbnailData>)

    @Query("DELETE FROM thumbnail_data")
    fun clearThumbnailData()
}

@Database(entities = [ThumbnailData::class], version = 1)
abstract class ComicDatabase : RoomDatabase() {
    abstract fun thumbnailDao(): ThumbnailDao
}