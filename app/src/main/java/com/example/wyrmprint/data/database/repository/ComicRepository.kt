package com.example.wyrmprint.data.database.repository

import androidx.paging.toLiveData
import com.example.wyrmprint.data.database.ThumbnailDao
import com.example.wyrmprint.data.model.ThumbnailData
import com.example.wyrmprint.data.remote.DragaliaLifeApi
import com.example.wyrmprint.data.remote.pager.ThumbnailDataSourceFactory
import dagger.Reusable
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@Reusable
class ComicRepository @Inject constructor(
    private var dragaliaApi: DragaliaLifeApi,
    private var compositeDisposable: CompositeDisposable,
    private var thumbnailDao: ThumbnailDao
) {

    /**
     * Get the comic information from [DragaliaLifeApi].
     */
    fun getComicDetail(comicId: Int) = dragaliaApi.fetchComicStripDetails(comicId)


    /**
     * Return a [ThumbnailDataSourceFactory]
     */
    fun getThumbnailDataSourceFactory() = ThumbnailDataSourceFactory(dragaliaApi, compositeDisposable, thumbnailDao)
}