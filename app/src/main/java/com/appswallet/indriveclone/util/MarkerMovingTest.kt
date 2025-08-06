package com.appswallet.indriveclone.util

import android.animation.Animator
import android.animation.ValueAnimator

import android.util.Log
import android.view.animation.LinearInterpolator
import com.appswallet.indriveclone.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

import com.google.maps.android.SphericalUtil


import javax.inject.Inject

private const val TAG = "MarkerMovingTest"
class MarkerMovingTest @Inject constructor() {


    private var moving = false
    private var marker: Marker? = null
    private var map: GoogleMap? = null
    private var listener: MarkerMovingListener? = null


    fun init(map: GoogleMap,listener: MarkerMovingListener){
        this.map = map
        this.listener = listener
    }



    fun startRider(
        path: List<LatLng>
    ){
        if (!moving){
            moving = true
            animateMarkerSmoothly(path)
        }
    }

    fun reCentre(){
        val position = marker?.position ?: LatLng(0.0,0.0)
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(position,18f))
    }


    private fun animateMarkerSmoothly(
        path: List<LatLng>
    ) {
        if (path.size < 2) return

        val option = MarkerOptions()
        option.position(path.first()).flat(true).anchor(0.5f,0.5f)
        option.title("Rider")
        option.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_rider))

        marker = map?.addMarker(option) ?: return

        var index = 0


        fun animateToNextPoint() {
            if (index >= path.size - 1) {
                moving = false
                listener?.onEnd()
                return
            }

            val start = path[index]
            val end = path[index + 1]

            val positionAnimator = ValueAnimator.ofFloat(0f, 1f)
            positionAnimator.duration = 5000L
            positionAnimator.interpolator = LinearInterpolator()

            positionAnimator.addUpdateListener { va ->
                val fraction = va.animatedValue as Float
                val newPosition = SphericalUtil.interpolate(start, end, fraction.toDouble())
                marker?.position = newPosition

            }

            val startRotation = marker?.rotation
            val endRotation = computeBearing(start, end)
            val rotationAnimator = ValueAnimator.ofFloat(0f, 1f)
            rotationAnimator.duration = 500L
            rotationAnimator.interpolator = LinearInterpolator()

            rotationAnimator.addUpdateListener { valueAnimator ->
                val fraction = valueAnimator.animatedValue as Float
                val newRotation = interpolateRotation(startRotation!!, endRotation, fraction)
                marker?.rotation = newRotation
            }



            positionAnimator.doOnEnd {
                index++
                animateToNextPoint()
            }

            positionAnimator.start()
            rotationAnimator.start()
        }

        animateToNextPoint()
    }

    fun ValueAnimator.doOnEnd(action: () -> Unit) {
        this.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {

            }

            override fun onAnimationEnd(p0: Animator) {
                action()
            }

            override fun onAnimationCancel(p0: Animator) {

            }

            override fun onAnimationRepeat(p0: Animator) {

            }
        })
    }

    fun computeBearing(from: LatLng, to: LatLng): Float {
        val heading = SphericalUtil.computeHeading(from, to)
        return ((heading + 360) % 360).toFloat()
    }

    fun interpolateRotation(start: Float, end: Float, fraction: Float): Float {
        var normalizedEnd = end
        var delta = (normalizedEnd - start + 360) % 360

        if (delta > 180) {
            delta -= 360
        }

        return (start + delta * fraction + 360) % 360
    }




    interface MarkerMovingListener{
        fun onEnd()
    }
}