package com.appswallet.indriveclone.ui


import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.appswallet.indriveclone.App
import com.appswallet.indriveclone.data.OrderRepo
import com.appswallet.indriveclone.databinding.ActivityMainBinding
import com.appswallet.indriveclone.model.Order
import com.appswallet.indriveclone.ui.adapter.OrderAdapter
import com.appswallet.indriveclone.ui.dialogs.OrderDialog
import com.google.type.Color
import javax.inject.Inject

private const val TAG = "MainActivityXXX"
class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var orderRepo: OrderRepo

    private val adapter by lazy {
        OrderAdapter(this,mutableListOf<Order>(),::handleOrder)
    }

    private var lat = 0.0
    private var lng = 0.0


    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if (it){
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("lat",lat)
            intent.putExtra("lng",lng)
            startActivity(intent)
        }else{
            Toast.makeText(this,"need location permission", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(scrim = android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(scrim = android.graphics.Color.TRANSPARENT)
        )
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        (application as App).orderComponent.inject(this)

        val data = orderRepo.getData()

        Log.d(TAG, "onCreate: ${data.size}")
        adapter.updateData(data)
        binding.recycler.adapter = adapter


    }

    private fun handleOrder(order: Order){
        val dialog = OrderDialog()
        dialog.isCancelable = false
        dialog.order = order
        dialog.callback = ::handleTrackOrder
        dialog.show(supportFragmentManager,"dialog")
    }

    private fun handleTrackOrder(lat: Double,lng: Double){
        this.lat = lat
        this.lng = lng
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}