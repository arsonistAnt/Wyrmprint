package com.example.wyrmprint.data.remote.repository

import com.example.wyrmprint.data.remote.DragaliaLifeApi
import javax.inject.Inject

class ComicRepository @Inject constructor(private var dragaliaApi: DragaliaLifeApi) {

    fun getThumbnailPage(pageNum: Int) = dragaliaApi.fetchComicStripPage(pageNum)

    fun getComicDetail(comicId: Int) = dragaliaApi.fetchComicStripDetails(comicId)
}