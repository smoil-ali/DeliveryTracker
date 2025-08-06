package com.appswallet.indriveclone.ui.viewModels


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appswallet.indriveclone.data.mapRepo.MapRepo
import com.appswallet.indriveclone.sealedModel.MapDirectionApiState

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


private const val TAG = "MapViewModelXXX"
class MapViewModel (private val mapRepo: MapRepo): ViewModel() {

     val latLngState = MutableStateFlow(listOf<LatLng>())

    init {
        viewModelScope.launch {
            mapRepo.pointState
                .collect {
                    when(it){
                        is MapDirectionApiState.Failed -> {
                            Log.d(TAG, "api failed ${it.msg}")
                            latLngState.emit(listOf())
                        }
                        is MapDirectionApiState.Success -> {
                            val list = decodePolyline(it.points)
                            latLngState.emit(list)
                        }
                    }
                }
        }
    }

    fun fetchLatLngList(origin: String,destination: String,apiKey: String){
        viewModelScope.launch {
            mapRepo.callDirectionApi(origin,destination,apiKey)
        }
    }

    private fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if ((result and 1) != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if ((result and 1) != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(lat / 1E5, lng / 1E5)
            poly.add(p)
        }

        return poly
    }
}