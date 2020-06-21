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
    var isFavorite: Boolean = false
)

@Entity(tableName = "thumbnail_favorites")
data class ThumbnailFavorite(
    @PrimaryKey
    val comicId: Int,
    val comicTitle: String,
    val comicNumber: Int,
    val comicUrl: String,
    val thumbnailLarge: String,
    val thumbnailSmall: String
)

interface ModelUtils {
    companion object {
        /**
         * Create an empty Thumbnail Favorite object.
         */
        fun createEmptyThumbnailFavorite() = ThumbnailFavorite(-1, "", -1, "", "", "")

        /**
         * Create an empty [ThumbnailData] object.
         */
        fun createEmptyThumbnailData() = ThumbnailData(-1, "", -1, "", -1, "", "")
    }
}

/**
 * Wrap a [ThumbnailData] object into a [ThumbnailItemView] and return it.
 */
fun ThumbnailData.toThumbnailItemView() = ThumbnailItemView(this, true)
    .apply { identifier = this@toThumbnailItemView.comicId.toLong() }

/**
 * Convert thumbnail data to [ThumbnailFavorite] objects.
 */
fun ThumbnailData.toFavoriteThumbnail() = ThumbnailFavorite(
    this.comicId,
    this.comicTitle,
    this.comicNumber,
    this.comicUrl,
    this.thumbnailLarge,
    this.thumbnailSmall
)

/**
 * Convert a list of [ThumbnailFavorite] to a list of [ThumbnailItemView] objects.
 */
fun List<ThumbnailFavorite>.toThumbnailItemView() = this.map {
    val thumbnailData = ThumbnailData(
        it.comicId,
        it.comicTitle,
        it.comicNumber,
        "",
        -1,
        it.thumbnailLarge,
        it.thumbnailSmall
    )
    // Set the identifier to the comic ID.
    ThumbnailItemView(thumbnailData).apply { identifier = it.comicId.toLong() }
}
