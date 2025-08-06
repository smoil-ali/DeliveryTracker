package com.appswallet.indriveclone.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.appswallet.indriveclone.data.mapRepo.MapRepo


class MapViewModelFactory(
    private val repo: MapRepo
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MapViewModel(repo) as T
    }

}