package com.example.wyrmprint.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

/**
 * A view model class for the ComicPagerFragment class.
 */
class ComicPagerViewModel @Inject constructor() : ViewModel() {

    // Live data that assists in keeping certain configurations which are dependent on orientation changes. (e.g. re-loading images, image resizing)
    private val _orientation = MutableLiveData<Int>()
    val orientation: LiveData<Int>
        get() = _orientation

    //Live data that assists in toggling between system ui visibilities.
    private val _systemUiVisible = MutableLiveData<Boolean>()
    val systemUiVisible: LiveData<Boolean>
        get() = _systemUiVisible

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
}