package com.appswallet.indriveclone.data

import android.util.Log
import com.appswallet.indriveclone.model.Order
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val TAG = "OrderOperation"
class OrderOperation @Inject constructor() {

    private val db = Firebase.firestore
    val riderStateFlow = MutableStateFlow(listOf<Order>())



    fun getOrders(){
        db.collection("orders")
            .orderBy("time")
            .addSnapshotListener { snapshot , error ->
                val list = mutableListOf<Order>()
                if (error == null){
                    val shots = snapshot?.documents
                    for (doc in shots!!){
                        val data = doc.toObject(Order::class.java)
                        list.add(data!!)
                        Log.d(TAG, "getOrdersForRider: $data")
                    }
                    riderStateFlow.value = list
                }else{
                    Log.d(TAG, "getOrdersForRider: reading failed ${error.message}")
                }
            }
    }


    suspend fun placeOrder(order: Order): Boolean = suspendCoroutine { continuation ->
        db.collection("orders")
            .add(order)
            .addOnSuccessListener { ref ->
                Log.d(TAG, "placeOrder: data added")
                continuation.resume(true)
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "placeOrder: data added failed ${e.message}")
                continuation.resume(false)
            }

    }


}