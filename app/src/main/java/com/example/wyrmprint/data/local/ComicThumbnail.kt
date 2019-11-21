package com.example.wyrmprint.data.local

import com.squareup.moshi.Json

/**
 * Json data class that holds thumbnail information on the comic strip.
 */
data class ComicThumbnail(
    @field:Json(name = "id") override var id: Int,
    @field:Json(name = "title") override var title: String,
    @field:Json(name = "episode_num") override var episodeNumber: Int,
    @field:Json(name = "main") override var comicUrl: String,
    @field:Json(name = "thumbnail_s") val thumbnailSmall: String,
    @field:Json(name = "thumbnail_l") val thumbnailLarge: String
) : ComicItem