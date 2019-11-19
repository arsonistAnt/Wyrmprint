package com.example.wyrmprint.data.local

import com.squareup.moshi.Json

/**
 * JSON data class that holds ordinal details for a [ComicStrip].
 */
data class SequentialComicStrip(
    @field:Json(name = "id") override var id: Int,
    @field:Json(name = "episode_num") override var episodeNumber: Int,
    override var title: String = "",
    override var comicUrl: String = ""
) : ComicItem
