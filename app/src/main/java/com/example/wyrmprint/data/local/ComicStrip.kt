package com.example.wyrmprint.data.local

import com.squareup.moshi.Json

/**
 *  JSON data class for comic strip details.
 */
data class ComicStrip(
    @field:Json(name = "id") override var id: Int,
    @field:Json(name = "title") override var title: String,
    @field:Json(name = "episode_num") override var episodeNumber: Int,
    @field:Json(name = "cartoon") override var comicUrl: String,
    @field:Json(name = "prev_cartoon") val prevStrip: SequentialComicStrip,
    @field:Json(name = "next_cartoon") val nextStrip: SequentialComicStrip
) : ComicItem