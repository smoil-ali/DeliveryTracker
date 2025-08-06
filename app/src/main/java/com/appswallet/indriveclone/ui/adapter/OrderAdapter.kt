package com.appswallet.indriveclone.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appswallet.indriveclone.databinding.ItemOrderBinding
import com.appswallet.indriveclone.model.Order


class OrderAdapter(
    private val context: Context,
    private val list: MutableList<Order>,
    private val callback: (Order) -> Unit
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ItemOrderBinding.inflate(inflater,parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){
            holder.binding.orderName.text = model.name
            holder.binding.price.text = model.price

            holder.binding.orderBtn.setOnClickListener {
                callback(model)
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateData(list: List<Order>){
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    class MyViewHolder(val binding: ItemOrderBinding): RecyclerView.ViewHolder(binding.root)
}