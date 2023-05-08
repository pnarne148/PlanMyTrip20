package com.example.planmytrip20.ui.itinerary.maps

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.planmytrip20.R
import com.example.planmytrip20.api.MapApiService
import com.example.planmytrip20.classes.GDirectionsAPIResponse
import com.example.planmytrip20.classes.GMapApiResponseData
import com.example.planmytrip20.classes.ItineraryLocation
import com.example.planmytrip20.classes.Result
import com.example.planmytrip20.classes.Route
import com.example.planmytrip20.databinding.FragmentMapBottomSheetBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.GsonBuilder
import com.google.maps.android.PolyUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MapBottomSheetFragment : BottomSheetDialogFragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentMapBottomSheetBinding
    private lateinit var mMap: GoogleMap

    private lateinit var mapType: String
    private lateinit var source: ItineraryLocation
    private lateinit var destination: ItineraryLocation

    private lateinit var routes : List<Route>
    private var displayRoute = false

    private lateinit var nearByPlaces : List<Result>
    private var displayNearByPlaces = false

    lateinit var polylineOptions : PolylineOptions
    lateinit var pathCoordinates : MutableList<LatLng>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {


        binding = FragmentMapBottomSheetBinding.inflate(layoutInflater, container, false)

        // Parsing values
        mapType = arguments?.getString("mapType").toString()
        source = GsonBuilder().create().fromJson(arguments?.getString("source"), ItineraryLocation::class.java)
        destination = GsonBuilder().create().fromJson(arguments?.getString("destination"), ItineraryLocation::class.java)
        fetchDirections()
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.bottomSheetHeader.text = "Directions & Places - Map view"
        binding.source.text = "Source: ${source.latLng.latitude}, ${source.latLng.longitude}"
        binding.destination.text = "Destination: ${destination.latLng.latitude}, ${destination.latLng.longitude}"

        return binding.root
    }

    companion object {
        const val TAG = "MapBottomSheetFragment"
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.clear()
        mMap.addMarker(
            MarkerOptions()
                .position(source.latLng)
                .title(source.name).icon(
                    BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_GREEN)))
        mMap.addMarker(
            MarkerOptions()
                .position(destination.latLng)
                .title(destination.name).icon(
                BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_RED)))
        val cameraPosition = CameraPosition.Builder()
            .target(source.latLng)
            .zoom(8f)
            .build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        if(displayRoute){
            polylineOptions = PolylineOptions()
            val polylinePoints = routes[0].overview_polyline.points
            pathCoordinates = PolyUtil.decode(polylinePoints)
            val boundsBuilder = LatLngBounds.Builder()
            pathCoordinates.forEach { coordinate ->
                boundsBuilder.include(coordinate)
                polylineOptions.add(coordinate)
            }
            val bounds = boundsBuilder.build()
            val padding = 100 // Adjust this as needed to create the desired amount of padding around the polyline
            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
            mMap.animateCamera(cameraUpdate)
            mMap.addPolyline(polylineOptions)
        }
        if(displayNearByPlaces && mapType == "restaurant"){
            for (place in nearByPlaces) {
                mMap.addMarker(MarkerOptions().position(LatLng(place.geometry.location.lat, place.geometry.location.lng)).title(place.name)
                    .icon(bitmapDescriptorFromVector(this.binding.root.context, R.drawable.restaurant)))
            }
        } else if(displayNearByPlaces && mapType =="gas_station"){
            for (place in nearByPlaces) {
                mMap.addMarker(MarkerOptions().position(LatLng(place.geometry.location.lat, place.geometry.location.lng)).title(place.name)
                    .icon(bitmapDescriptorFromVector(this.binding.root.context, R.drawable.gas)))
            }
        }

    }

    private fun  bitmapDescriptorFromVector(context: Context, vectorResId:Int):BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas =  Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun fetchDirections(){
        val urlString = "https://maps.googleapis.com/"
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(urlString)
            .build().create(MapApiService::class.java)
        val drivingMode = if(mapType != "BICYCLE"){
            "DRIVING"
        } else {
            "BICYCLE"
        }

        if(mapType == "restaurant" || mapType == "gas_station"){
            fetchNearByPlaces()
        }

        val retrofitData = retrofitBuilder.getDirections(
            "AIzaSyD0IAwuCADIsl-MFp1yhhGWBXxgjlUjbFw",
            "${source.latLng.latitude},${source.latLng.longitude}",
            "${destination.latLng.latitude},${destination.latLng.longitude}",drivingMode)
//            "Montreal","DRIVING")

        retrofitData.enqueue(object : Callback<GDirectionsAPIResponse?> {
            override fun onResponse(
                call: Call<GDirectionsAPIResponse?>,
                response: Response<GDirectionsAPIResponse?>,
            ) {
                val responseBody = response.body()!!
                routes = responseBody.routes
                if(routes.isNotEmpty()){
                    displayRoute = true
                }
                onMapReady(mMap)
            }
            override fun onFailure(call: Call<GDirectionsAPIResponse?>, t: Throwable) {
                TODO("Not yet implemented")
                Log.i(TAG, "Failure Message: ${t.message}")
            }
        })
    }

    private fun fetchNearByPlaces() {
        val urlString =  "https://maps.googleapis.com/"
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(urlString)
            .build().create(MapApiService::class.java)

        val retrofitData = retrofitBuilder.getNearByPlaces(
            "AIzaSyD0IAwuCADIsl-MFp1yhhGWBXxgjlUjbFw",
            "${source.latLng.latitude},${source.latLng.longitude}",
            mapType, 150000)

        retrofitData.enqueue(object : Callback<GMapApiResponseData?> {
            override fun onResponse(
                call: Call<GMapApiResponseData?>,
                response: Response<GMapApiResponseData?>,
            ) {
                val responseBody = response.body()!!
                nearByPlaces =  responseBody.results
                Log.i(TAG, "Near By Places: ${nearByPlaces.size}")
                if(nearByPlaces.isNotEmpty()){
                    displayNearByPlaces = true
                }
                onMapReady(mMap)
            }
            override fun onFailure(call: Call<GMapApiResponseData?>, t: Throwable) {
                TODO("Not yet implemented")
                Log.i(TAG, "Failure Message: ${t.message}")
            }
        })

    }
}