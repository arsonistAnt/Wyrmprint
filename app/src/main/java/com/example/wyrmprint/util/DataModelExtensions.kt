package com.example.wyrmprint.util

import com.example.wyrmprint.data.model.ComicThumbnailData
import com.example.wyrmprint.ui.browse.viewholder.ThumbnailItemView

/*
Extension function for data model classes.
 */


/**
 * Wrap a [ComicThumbnailData] object into a [ThumbnailItemView] and return it.
 */
fun ComicThumbnailData.toThumbnailItemView() = ThumbnailItemView(this)