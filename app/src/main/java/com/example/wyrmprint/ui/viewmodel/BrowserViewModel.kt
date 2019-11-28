package com.example.wyrmprint.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.example.wyrmprint.data.model.ComicThumbnailData
import com.example.wyrmprint.data.remote.repository.ComicRepository
import javax.inject.Inject

class BrowserViewModel @Inject constructor(private val comicRepo: ComicRepository) : ViewModel() {
    // PagedList to load thumbnail comic items.
    private val _thumbnailPageList = comicRepo.getThumbnailPageDataSource()
    val thumbnailDataItemPageList: LiveData<PagedList<ComicThumbnailData>>
        get() = _thumbnailPageList

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
}