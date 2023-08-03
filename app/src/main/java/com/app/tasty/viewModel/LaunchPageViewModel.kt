package com.app.tasty.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LaunchPageViewModel : ViewModel() {
    private val _geoLocationLd = MutableLiveData<String>()
    val geoLocationLd: LiveData<String>
        get() = _geoLocationLd

    fun getAddress(address: String) {
        _geoLocationLd.value = address
    }
}