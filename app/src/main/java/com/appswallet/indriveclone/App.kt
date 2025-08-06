package com.appswallet.indriveclone

import android.app.Application
import com.appswallet.indriveclone.di.DaggerOrderComponent
import com.appswallet.indriveclone.di.OrderComponent


class App: Application() {

    lateinit var orderComponent: OrderComponent

    override fun onCreate() {
        super.onCreate()



        orderComponent = DaggerOrderComponent
            .create()



    }
}