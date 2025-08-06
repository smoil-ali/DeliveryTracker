package com.appswallet.indriveclone.di


import com.appswallet.indriveclone.ui.MainActivity
import com.appswallet.indriveclone.ui.MapActivity
import com.appswallet.indriveclone.ui.dialogs.OrderDialog
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [OrderModule::class])
interface OrderComponent {

    fun inject(activity: MainActivity)
    fun inject(orderDialog: OrderDialog)
    fun inject(activity: MapActivity)
}