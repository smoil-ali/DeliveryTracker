package com.appswallet.indriveclone.ui.dialogs


import android.content.Intent
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast


import androidx.constraintlayout.widget.Constraints
import androidx.fragment.app.DialogFragment
import com.appswallet.indriveclone.App
import com.appswallet.indriveclone.data.OrderOperation

import com.appswallet.indriveclone.databinding.FragmentOrderDialogBinding
import com.appswallet.indriveclone.model.Order



import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

import kotlinx.coroutines.launch
import javax.inject.Inject

class OrderDialog : DialogFragment() {

    private var _binding: FragmentOrderDialogBinding? = null
    private val binding get() = _binding!!

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    lateinit var order: Order

    lateinit var callback: (Double, Double) -> Unit

    @Inject
    lateinit var orderOperation: OrderOperation




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOrderDialogBinding.inflate(inflater,container,false)


        (requireActivity().application as App).orderComponent.inject(this)
        binding.name.text = order.name
        binding.price.text = order.price

        binding.cancel.setOnClickListener {
            dismiss()
        }

        binding.confirmBtn.setOnClickListener {
            coroutineScope.launch {

                binding.main.visibility = View.INVISIBLE
                binding.loading.visibility = View.VISIBLE

                order.time = System.currentTimeMillis()
                val status = orderOperation.placeOrder(order)

                binding.loading.visibility = View.GONE

                if (status){
                    binding.trackOrder.visibility = View.VISIBLE
                    binding.alertMsg.visibility = View.VISIBLE
                }else{
                    binding.main.visibility = View.VISIBLE
                    Toast.makeText(requireContext(),"Failed", Toast.LENGTH_SHORT).show()
                }

            }
        }

        binding.trackOrder.setOnClickListener {

            if (::callback.isInitialized)
                callback(order.lat,order.lng)
            dismiss()
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(Constraints.LayoutParams.MATCH_PARENT,
            Constraints.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        coroutineScope.cancel()
    }


}