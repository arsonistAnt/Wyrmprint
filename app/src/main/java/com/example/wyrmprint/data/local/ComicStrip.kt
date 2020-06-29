package com.example.wyrmprint.data.local

import com.example.wyrmprint.data.model.ThumbnailData
import com.example.wyrmprint.data.model.ThumbnailUrl
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


/**
 * Convert [ComicStrip] to [ThumbnailData]
 *
 * @return a [ThumbnailData] object.
 */
fun ComicStrip.toThumbnailData(): ThumbnailData =
    ThumbnailData(id, title, episodeNumber, comicUrl, 0, "", "")

/**
 * Update [ThumbnailData] with [thumbnailUrl] and return it.
 *
 * @param thumbnailUrl a [ThumbnailUrl] that contains thumbnail url's.
 * @return a [ThumbnailData] object with its large and small thumbnail url's updated.
 */
fun ComicStrip.toThumbnailData(thumbnailUrl: ThumbnailUrl): ThumbnailData = ThumbnailData(
    id,
    title,
    episodeNumber,
    comicUrl,
    0,
    thumbnailUrl.thumbnailLarge,
    thumbnailUrl.thumbnailSmall
)