package com.example.wyrmprint.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wyrmprint.injection.module.DaggerComicRepoModule
import com.example.wyrmprint.ui.browse.adapters.ThumbnailItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import timber.log.Timber

class BrowserViewModel : ViewModel() {
    // TODO: Injection
    private val comicRepo =
        DaggerComicRepoModule.builder()
            .build()
            .getComicRepo()
    // TODO: Injection
    private var disposables = CompositeDisposable()

    // Live Data
    private val _thumbnailPage = MutableLiveData<List<ThumbnailItem>>()
    val thumbnailPage: LiveData<List<ThumbnailItem>>
        get() = _thumbnailPage


    /**
     * Start a fetch process for a thumbnail page from the comic repository. After
     * the fetching is finished it is assigned to [thumbnailPage]'s value
     *
     * @param pageNum the page number for the thumbnail's
     */
    fun fetchThumbnailPage(pageNum: Int) {
        val thumbnailObservable = comicRepo.getThumbnailPage(pageNum)
        thumbnailObservable.observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                Timber.e(it)
            }
            .subscribe {
                val thumbnailItems = it.map { ThumbnailItem(it) }
                _thumbnailPage.value = thumbnailItems
            }
            .addTo(disposables)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}