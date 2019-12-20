package com.example.wyrmprint.data.model

import androidx.paging.DataSource
import androidx.room.*

@Entity(tableName = "thumbnail_data")
data class ThumbnailData(
    @PrimaryKey
    val comicId: Int,
    val comicTitle: String,
    val comicNumber: Int,
    val comicUrl: String,
    val thumbnailSmallUrl: String,
    val thumbnailLargeUrl: String
)

@Dao
interface ThumbnailDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(thumbnails: List<ThumbnailData>)

    @Query("SELECT * FROM thumbnail_data ORDER BY comicNumber DESC")
    fun getThumbnailPages(): DataSource.Factory<Int, ThumbnailData>

    @Query("DELETE FROM thumbnail_data")
    suspend fun deleteAll()
}