package com.example.wyrmprint.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wyrmprint.data.database.repository.ComicRepository
import com.example.wyrmprint.data.local.ComicStrip
import com.example.wyrmprint.data.model.NetworkState
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
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

    private val _comicDetailsState = MutableLiveData<NetworkState<ComicStrip>>()
    val comicDetailsState: LiveData<NetworkState<ComicStrip>>
        get() = _comicDetailsState

    var prevComicId : Int = -1
    var nextComicId : Int = -1

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
            .map { NetworkState.success(it) }
            .startWith (NetworkState.inProgress<ComicStrip>().apply { _comicDetailsState.postValue(this) })
            .onErrorReturn {
                val state = NetworkState.failure<ComicStrip>(it)
                _comicDetailsState.postValue(state)
                state
            }
            .doOnComplete { print("Completed") }
            .subscribe{
                _comicDetailsState.postValue(it)
            }.addTo(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}