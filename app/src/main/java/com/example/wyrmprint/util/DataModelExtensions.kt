package com.example.wyrmprint.util

import com.example.wyrmprint.data.model.ComicThumbnailData
import com.example.wyrmprint.data.model.ThumbnailData
import com.example.wyrmprint.ui.browse.viewholder.ThumbnailItemView

/*
Extension function for data model classes.
 */


/**
 * Wrap a [ThumbnailData] object into a [ThumbnailItemView] and return it.
 */
fun ThumbnailData.toThumbnailItemView() = ThumbnailItemView(this)

fun List<ComicThumbnailData>.toThumbnailData() = this.map { it.toThumbnailData() }

fun ComicThumbnailData.toThumbnailData() = this.run {
    ThumbnailData(id, title, episodeNumber, comicUrl, thumbnailSmall, thumbnailLarge)
}