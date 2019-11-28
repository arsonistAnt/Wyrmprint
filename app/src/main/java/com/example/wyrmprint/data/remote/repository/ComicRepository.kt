package com.example.wyrmprint.data.remote.repository

import androidx.paging.toLiveData
import com.example.wyrmprint.data.remote.DragaliaLifeApi
import com.example.wyrmprint.data.remote.pager.DataSourceCallback
import com.example.wyrmprint.data.remote.pager.ThumbnailDataSourceFactory
import dagger.Reusable
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@Reusable
class ComicRepository @Inject constructor(
    private var dragaliaApi: DragaliaLifeApi,
    private var compositeDisposable: CompositeDisposable
) {
    private var onLoadThumbnailInitial = {}
    private var onLoadThumbnailAfter = {}

    fun getComicDetail(comicId: Int) = dragaliaApi.fetchComicStripDetails(comicId)

    fun getThumbnailPageDataSource() =
        ThumbnailDataSourceFactory(dragaliaApi, compositeDisposable).apply {
            dataSourceListener = object : DataSourceCallback() {
                override fun onLoadAfter() {
                    onLoadThumbnailAfter()
                }

                override fun onLoadInitial() {
                    onLoadThumbnailInitial()
                }
            }
        }.toLiveData(
            pageSize = 2
        )

    /**
     * Set a function call back when the thumbnail data source has finished loading the  initial data.
     *
     * @see getThumbnailPageDataSource
     * @see ThumbnailDataSourceFactory
     * @param action the function callback.
     */
    fun setThumbnailLoadedInitialCallback(action: () -> Unit) {
        onLoadThumbnailInitial = action
    }

    /**
     * Set a function call back when the thumbnail data source has finished loading the after data.
     *
     * @see getThumbnailPageDataSource
     * @see ThumbnailDataSourceFactory
     * @param action the function callback.
     */
    fun setThumbnailLoadedAfterCallback(action: () -> Unit) {
        onLoadThumbnailAfter = action
    }
}