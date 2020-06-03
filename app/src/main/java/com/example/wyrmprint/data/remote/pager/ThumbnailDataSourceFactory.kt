package com.example.wyrmprint.data.remote.pager

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.example.wyrmprint.data.database.ThumbnailDao
import com.example.wyrmprint.data.model.NetworkState
import com.example.wyrmprint.data.model.NetworkStatus
import com.example.wyrmprint.data.model.ThumbnailData
import com.example.wyrmprint.data.remote.DragaliaLifeApi
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Factory class that handles the creation of [ThumbnailComicDataSource] objects.
 */
class ThumbnailDataSourceFactory @Inject constructor(
    private val dragaliaApi: DragaliaLifeApi,
    private val disposables: CompositeDisposable,
    private val thumbnailDao: ThumbnailDao
) : DataSource.Factory<Int, ThumbnailData>() {

    // The current thumbnail data source that has been created.
    var currThumbnailDataSource = MutableLiveData<ThumbnailComicDataSource>()

    override fun create(): DataSource<Int, ThumbnailData> {
        return ThumbnailComicDataSource(dragaliaApi, disposables, thumbnailDao).apply {
            currThumbnailDataSource.postValue(this)
        }
    }
}

