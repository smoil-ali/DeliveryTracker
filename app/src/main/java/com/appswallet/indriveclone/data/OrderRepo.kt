package com.appswallet.indriveclone.data

import com.appswallet.indriveclone.model.Order
import javax.inject.Inject


class OrderRepo @Inject constructor() {
    fun getData(): List<Order>{
        return listOf(
            Order("Pizza","1500 Rs",10000L,31.50050783628196, 73.27216831759452),
            Order("Zinger Burger","350 Rs",10000L,31.50050783628196, 73.27216831759452),
            Order("Petty Burger","280 Rs",10000L,31.50050783628196, 73.27216831759452),
            Order("Pizza Roll","250 Rs",10000L,31.50050783628196, 73.27216831759452),
            Order("Wings Bucket","600 Rs",10000L,31.50050783628196, 73.27216831759452),
            Order("Chicken Bucket","2100 Rs",10000L,31.50050783628196, 73.27216831759452),
        )
    }
}