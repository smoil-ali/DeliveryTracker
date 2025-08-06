package com.appswallet.indriveclone.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitClient @Inject constructor() {

    private val baseURL = "https://maps.googleapis.com/maps/api/"

    val directionsApi: DirectionService by lazy {
        Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DirectionService::class.java)
    }

}