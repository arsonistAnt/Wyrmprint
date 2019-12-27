package com.example.wyrmprint.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.example.wyrmprint.data.database.repository.ComicRepository
import com.example.wyrmprint.data.model.ThumbnailData
import javax.inject.Inject

class BrowserViewModel @Inject constructor(private val comicRepo: ComicRepository) : ViewModel() {
    // PagedList to load thumbnail comic items.
    private val _thumbnailPageList = comicRepo.getThumbnailPageDataSource()
    val thumbnailDataItemPageList: LiveData<PagedList<ThumbnailData>>
        get() = _thumbnailPageList

    // Live data to keep track on
    private val _loadedLastPage = MutableLiveData<Boolean>(false)
    val loadedLastPage: LiveData<Boolean>
        get() = _loadedLastPage

    /**
     * Set the function callback for when the initial thumbnail data has finished loading.
     *
     * @param action the function callback.
     */
    fun setOnInitialLoadedThumbnail(action: () -> Unit) {
        comicRepo.setThumbnailLoadedInitialCallback(action)
    }

    /**
     * Set the function callback for when the after thumbnail data has finished loading.
     *
     * @param action the function callback.
     */
    fun setOnLoadedMoreThumbnail(action: () -> Unit) {
        comicRepo.setThumbnailLoadedAfterCallback(action)
    }


    /**
     * Called when reaching the last page of the paging list.
     */
    fun onLastPageLoaded() = run { _loadedLastPage.postValue(true) }

    /**
     * Called when resetting or adding more to the paging list.
     */
    fun resetPageLoadedState() = run { _loadedLastPage.postValue(false) }

}