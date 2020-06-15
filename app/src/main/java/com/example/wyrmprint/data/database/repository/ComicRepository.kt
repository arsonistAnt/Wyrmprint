package com.example.wyrmprint.data.database.repository

import com.example.wyrmprint.data.database.ThumbnailCacheDao
import com.example.wyrmprint.data.database.ThumbnailFavoritesDao
import com.example.wyrmprint.data.remote.DragaliaLifeApi
import com.example.wyrmprint.data.remote.pager.ThumbnailDataSourceFactory
import dagger.Reusable
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@Reusable
class ComicRepository @Inject constructor(
    private var dragaliaApi: DragaliaLifeApi,
    private var compositeDisposable: CompositeDisposable,
    private var thumbnailCacheDao: ThumbnailCacheDao,
    private var favoritesDao: ThumbnailFavoritesDao
) {

    /**
     * Get the comic information from [DragaliaLifeApi].
     */
    fun getComicDetail(comicId: Int) = dragaliaApi.fetchComicStripDetails(comicId)


    /**
     * Return a [ThumbnailDataSourceFactory]
     */
    fun getThumbnailDataSourceFactory() = ThumbnailDataSourceFactory(dragaliaApi, compositeDisposable, thumbnailCacheDao)
}