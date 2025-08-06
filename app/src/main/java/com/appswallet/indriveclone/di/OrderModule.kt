package com.appswallet.indriveclone.di

import com.appswallet.indriveclone.data.mapRepo.MapRepo
import com.appswallet.indriveclone.data.network.RetrofitClient
import dagger.Module
import dagger.Provides

@Module
class OrderModule {


    @Provides
    fun getMapRepo(client: RetrofitClient): MapRepo{
        return MapRepo(client)
    }


}