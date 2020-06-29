package com.example.wyrmprint.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wyrmprint.data.database.repository.ComicRepository
import com.example.wyrmprint.data.local.ComicStrip
import com.example.wyrmprint.data.local.toThumbnailData
import com.example.wyrmprint.data.model.NetworkState
import com.example.wyrmprint.data.model.ThumbnailUrl
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A view model class for the ComicPagerFragment class.
 */
class ComicPagerViewModel @Inject constructor(
    private val comicRepo: ComicRepository,
    private val disposable: CompositeDisposable
) :
    ViewModel() {

    // Live data that assists in keeping certain configurations which are dependent on orientation changes. (e.g. re-loading images, image resizing)
    private val _orientation = MutableLiveData<Int>()
    val orientation: LiveData<Int>
        get() = _orientation

    //Live data that assists in toggling between system ui visibilities.
    private val _systemUiVisible = MutableLiveData<Boolean>()
    val systemUiVisible: LiveData<Boolean>
        get() = _systemUiVisible

    // Live data that determines if the comic detail has been saved to the favorite table.
    private val _isSavedToFavorites = MutableLiveData<Boolean>()
    val isSavedToFavorites: LiveData<Boolean>
        get() = _isSavedToFavorites

    private val _comicDetailsState = MutableLiveData<NetworkState<ComicStrip>>()
    val comicDetailsState: LiveData<NetworkState<ComicStrip>>
        get() = _comicDetailsState

    var prevComicId: Int = -1
    var nextComicId: Int = -1

    /**
     * Stores the current system configuration in the [_orientation] live data integer.
     *
     * @param currOrientation the current orientation configuration.
     */
    fun onOrientationChange(currOrientation: Int) {
        _orientation.value = currOrientation
    }

    /**
     * A setter for the [_systemUiVisible] live data boolean.
     *
     * @param show the boolean to set the [_systemUiVisible] value to.
     */
    fun showSystemUi(show: Boolean) {
        _systemUiVisible.value = show
    }

    /**
     * Request comic strip details based on id given.
     *
     * @param id unique identifier for the dragalia comic strip.
     */
    fun requestComicDetails(id: Int) {
        comicRepo.getComicDetail(id)
            .map {
                // A boolean that determines if this comic has already been saved to favorites.
                val savedToFavorites = comicRepo.isFavorited(it.id) == 1
                _isSavedToFavorites.postValue(savedToFavorites)
                NetworkState.success(it)
            }
            .startWith(
                NetworkState.inProgress<ComicStrip>().apply { _comicDetailsState.postValue(this) })
            .onErrorReturn {
                val state = NetworkState.failure<ComicStrip>(it)
                _comicDetailsState.postValue(state)
                state
            }
            .doOnComplete { print("Completed") }
            .subscribe {
                _comicDetailsState.postValue(it)
            }.addTo(disposable)
    }

    /**
     * Set true/false for [_isSavedToFavorites] and remove/add to the favorites in the comic repository.
     */
    fun toggleFavorites(comicStrip: ComicStrip) {
        val isFavorited = _isSavedToFavorites.value
        isFavorited?.let {
            viewModelScope.launch(Dispatchers.IO) {
                if (!isFavorited) {
                    val thumbnailUrl =
                        comicRepo.getThumbnailUrlFromCache(comicStrip.id) ?: ThumbnailUrl("", "")
                    comicRepo.saveFavoriteComic(comicStrip.toThumbnailData(thumbnailUrl))
                    comicRepo.updateThumbnailFavoritesField(listOf(comicStrip.id), true)
                    _isSavedToFavorites.postValue(true)
                } else {
                    comicRepo.removeFavoriteComicsById(listOf(comicStrip.id))
                    comicRepo.updateThumbnailFavoritesField(listOf(comicStrip.id), false)
                    _isSavedToFavorites.postValue(false)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}