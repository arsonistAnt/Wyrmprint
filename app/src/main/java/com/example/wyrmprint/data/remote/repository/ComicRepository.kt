package com.example.wyrmprint.data.remote.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.example.wyrmprint.data.model.ThumbnailDao
import com.example.wyrmprint.data.model.ThumbnailData
import com.example.wyrmprint.data.remote.DragaliaLifeApi
import com.example.wyrmprint.data.remote.pager.ThumbnailBoundaryCallback
import dagger.Reusable
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@Reusable
class ComicRepository @Inject constructor(
    private var dragaliaApi: DragaliaLifeApi,
    private var compositeDisposable: CompositeDisposable,
    private var thumbnailDao: ThumbnailDao
) {

    fun getComicDetail(comicId: Int) = dragaliaApi.fetchComicStripDetails(comicId)

    /**
     * Return a live data PagedList<ThumbnailData> object.
     */
    fun getThumbnailDataSourceFactory(): LiveData<PagedList<ThumbnailData>> {
        val callBack = ThumbnailBoundaryCallback(
            dragaliaApi,
            compositeDisposable,
            thumbnailDao
        )

        return thumbnailDao.getThumbnailPages().toLiveData(
            config = PagedList.Config.Builder()
                .setPageSize(2)
                .setEnablePlaceholders(false).build(),
            boundaryCallback = callBack
        )
    }

    /**
     * Delete the records in the thumbnail_data table.
     */
    suspend fun initRefresh() {
        thumbnailDao.deleteAll()
    }
}