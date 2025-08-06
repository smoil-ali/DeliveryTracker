package com.appswallet.indriveclone.ui

import android.Manifest

import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle

import android.os.Looper
import android.util.Log
import android.view.View

import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.appswallet.indriveclone.App
import com.appswallet.indriveclone.BuildConfig
import com.appswallet.indriveclone.R
import com.appswallet.indriveclone.data.mapRepo.MapRepo
import com.appswallet.indriveclone.util.MarkerMovingTest
import com.appswallet.indriveclone.databinding.ActivityMapBinding
import com.appswallet.indriveclone.ui.dialogs.ArriveDialog
import com.appswallet.indriveclone.ui.viewModels.MapViewModel
import com.appswallet.indriveclone.ui.viewModels.MapViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap

import com.google.android.gms.maps.SupportMapFragment


import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.CompletableDeferred




import kotlinx.coroutines.launch


import javax.inject.Inject


private const val TAG = "MapActivityXXX"

class MapActivity : AppCompatActivity(), MarkerMovingTest.MarkerMovingListener {

    private val binding by lazy {
        ActivityMapBinding.inflate(layoutInflater)
    }


    @Inject
    lateinit var mapRepo: MapRepo

    @Inject
    lateinit var markerMoving: MarkerMovingTest


    private lateinit var mapViewModelFactory: MapViewModelFactory
    private val mapViewModel: MapViewModel by lazy {
        ViewModelProvider(this,mapViewModelFactory)[MapViewModel::class.java]
    }



    private lateinit var map: GoogleMap
    private var polyline: Polyline? = null
    private var destinationLatLng = LatLng(0.0,0.0)
    private var currentLatLng = LatLng(0.0,0.0)
    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        3000
    ).apply {
        setMinUpdateIntervalMillis(1000)
        setMaxUpdateDelayMillis(10000L)
    }.build()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient



    private val locationCallback = object: LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {


            val location = p0.lastLocation ?: return

            val newLatLng = LatLng(location.latitude, location.longitude)

            val start = Location("start").apply {
                latitude = newLatLng.latitude
                longitude = newLatLng.longitude
            }

            val end = Location("start").apply {
                latitude = newLatLng.latitude
                longitude = newLatLng.longitude
            }

            val distance = start.distanceTo(end)
            Log.d(TAG, "onLocationResult: $distance")
            if (distance > 5){
                currentLatLng = newLatLng
                drawPolyLine()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(scrim = Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(scrim = Color.TRANSPARENT)
        )
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        (application as App).orderComponent.inject(this)
        mapViewModelFactory = MapViewModelFactory(mapRepo)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val lat = intent.extras?.getDouble("lat") ?: 0.0
        val lng = intent.extras?.getDouble("lng") ?: 0.0

        destinationLatLng = LatLng(lat, lng)

        lifecycleScope.launch {

            binding.reCentreBtn.visibility = View.INVISIBLE
            val task = CompletableDeferred<Unit>()
            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync{ p0 ->
                Log.d(TAG, "onCreate: map is ready")
                map = p0
                addMarkerOnRiderLocation()
                task.complete(Unit)
            }

            task.await()

            binding.reCentreBtn.visibility = View.VISIBLE
            markerMoving.init(map,this@MapActivity)
            getCurrentLocation()
            // getUpdateLocations()

        }

        lifecycleScope.launch {
            mapViewModel.latLngState.collect {

                if (it.isNotEmpty()){
                    drawPolyLine(it, getColor(R.color.color4))
                    markerMoving.startRider(it)
                }

            }
        }

        binding.reCentreBtn.setOnClickListener {
            markerMoving.reCentre()
        }

        binding.back.setOnClickListener {
            finish()
        }



    }



    private fun getUpdateLocations(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private fun getCurrentLocation(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                if (location != null){
                    currentLatLng = LatLng(location.latitude,location.longitude)
                    Log.d(TAG, "onCreate: current location get")
                    addMarkerOnCurrentLocation()
                    drawPolyLine()
                }else{
                    Toast.makeText(this,"location not available", Toast.LENGTH_SHORT).show()
                }

            }

        }
    }

    private fun addMarkerOnRiderLocation(){
        val option = MarkerOptions()
        option.position(destinationLatLng)
        option.title("Pick Point")
        map.addMarker(option)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(destinationLatLng,18f))
    }

    private fun addMarkerOnCurrentLocation(){
        val option = MarkerOptions()
        option.position(currentLatLng)
        option.title("Customer")
        map.addMarker(option)
    }

    private fun drawPolyLine(){
        val origin = "${currentLatLng.latitude},${currentLatLng.longitude}"
        val destination = "${destinationLatLng.latitude},${destinationLatLng.longitude}"
        mapViewModel.fetchLatLngList(destination,origin, BuildConfig.MAPS_API_KEY)
    }

    fun drawPolyLine(path: List<LatLng>,color: Int = Color.BLUE){
        if (polyline == null){
            val polylineOptions = PolylineOptions()
                .addAll(path)
                .color(color)
                .width(20f)
                .geodesic(true)

            polyline = map.addPolyline(polylineOptions)

        }else{
            polyline?.points = path
        }

    }

    override fun onEnd() {
        if (!isDestroyed && !isFinishing){
            val dialog = ArriveDialog()
            dialog.isCancelable = true
            dialog.show(supportFragmentManager,"dialog")
        }

    }
}