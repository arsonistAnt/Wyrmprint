package com.example.wyrmprint.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class ComicPagerViewModel @Inject constructor() : ViewModel() {
    private val _orientation = MutableLiveData<Int>()
    val orientation: LiveData<Int>
        get() = _orientation

    fun onOrientationChange(orientation: Int) {
        _orientation.value = orientation
    }
}