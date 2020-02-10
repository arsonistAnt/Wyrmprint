package com.example.wyrmprint.data.database.repository

import androidx.paging.toLiveData
import com.example.wyrmprint.data.database.ThumbnailDao
import com.example.wyrmprint.data.remote.DragaliaLifeApi
import com.example.wyrmprint.data.remote.pager.DataSourceCallback
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
    private var onLoadThumbnailInitial = {}
    private var onLoadThumbnailAfter = {}

    // Thumbnail data source factory.
    private val thumbnailDataSourceFactory by lazy {
        ThumbnailDataSourceFactory(dragaliaApi, compositeDisposable, thumbnailDao).apply {
            dataSourceListener = object : DataSourceCallback() {
                override fun onLoadAfter() {
                    onLoadThumbnailAfter()
                }

                override fun onLoadInitial() {
                    onLoadThumbnailInitial()
                }
            }
        }
    }

    fun getComicDetail(comicId: Int) = dragaliaApi.fetchComicStripDetails(comicId)

    fun getThumbnailPageDataSource() =
        thumbnailDataSourceFactory.toLiveData(
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

    /**
     * Invalidate paged list data and clear thumbnail cache in room, allowing to re-cache and update new data into the database.
     */
    fun invalidateThumbnailData(){
        thumbnailDataSourceFactory.mainDataSource.invalidate()
    }
}