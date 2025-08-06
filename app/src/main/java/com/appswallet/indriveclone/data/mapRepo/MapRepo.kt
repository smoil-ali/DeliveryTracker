package com.appswallet.indriveclone.data.mapRepo


import com.appswallet.indriveclone.data.network.RetrofitClient
import com.appswallet.indriveclone.sealedModel.MapDirectionApiState
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class MapRepo @Inject constructor(private val client: RetrofitClient) {

    val pointState = MutableStateFlow<MapDirectionApiState>(
        MapDirectionApiState.Success("")
    )

    suspend fun callDirectionApi(origin: String,destination: String,apiKey: String){
        val response = client.directionsApi.getDirections(
            origin,
            destination,
            apiKey
        )
        if (response.isSuccessful){
            val points = response.body()?.routes?.firstOrNull()?.overviewPolyline?.points
            if (points != null){
                pointState.emit(
                    MapDirectionApiState.Success(points)
                )
            }else{
                pointState.emit(
                    MapDirectionApiState.Failed("points are empty or null")
                )
            }
        }else{
            pointState.emit(
                MapDirectionApiState.Failed(response.message()+" "+response.body()?.status)
            )
        }
    }


}