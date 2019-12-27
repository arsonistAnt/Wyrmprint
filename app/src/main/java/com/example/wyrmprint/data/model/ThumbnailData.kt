package com.example.wyrmprint.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.wyrmprint.ui.browse.viewholder.ThumbnailItemView

@Entity(tableName = "thumbnail_data")
data class ThumbnailData(
    @PrimaryKey
    val comicId: Int,
    val comicTitle: String,
    val comicNumber: Int,
    val comicUrl: String,
    var pageNumber: Int,
    val thumbnailLarge: String,
    val thumbnailSmall: String,
    var favorite: Boolean = false
)

/**
 * Wrap a [ThumbnailData] object into a [ThumbnailItemView] and return it.
 */
fun ThumbnailData.toThumbnailItemView() = ThumbnailItemView(this)
