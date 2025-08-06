package com.appswallet.indriveclone.model

data class Order(
    val name: String,
    val price: String,
    var time: Long,
    val lat: Double,
    val lng: Double
)
