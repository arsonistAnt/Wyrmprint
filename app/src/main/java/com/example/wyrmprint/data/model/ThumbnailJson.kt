package com.example.wyrmprint.data.model

import com.example.wyrmprint.data.local.ComicItem
import com.squareup.moshi.Json

/**
 * Json data class that holds thumbnail information on the comic strip.
 */
data class ThumbnailJson(
    @field:Json(name = "id") override var id: Int,
    @field:Json(name = "title") override var title: String,
    @field:Json(name = "episode_num") override var episodeNumber: Int,
    @field:Json(name = "main") override var comicUrl: String,
    @field:Json(name = "thumbnail_l") val thumbnailLarge: String,
    @field:Json(name = "thumbnail_s") val thumbnailSmall: String

) : ComicItem


fun List<ThumbnailJson>.toThumbnailData(pageNum: Int) = this.map { thumbnailJson ->
    ThumbnailData(
        thumbnailJson.id,
        thumbnailJson.title,
        thumbnailJson.episodeNumber,
        thumbnailJson.comicUrl,
        pageNum,
        thumbnailJson.thumbnailLarge,
        thumbnailJson.thumbnailSmall
    )
}