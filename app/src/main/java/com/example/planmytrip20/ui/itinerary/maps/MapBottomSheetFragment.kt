package com.example.planmytrip20.ui.itinerary.maps

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.planmytrip20.R
import com.example.planmytrip20.api.MapApiService
import com.example.planmytrip20.classes.GDirectionsAPIResponse
import com.example.planmytrip20.classes.ItineraryLocation
import com.example.planmytrip20.classes.Route
import com.example.planmytrip20.databinding.FragmentMapBottomSheetBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
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
            val polylineOptions = PolylineOptions()
            Log.i(TAG, "Route length: ${routes.size}")
            Log.i(TAG, "Route steps length: ${routes[0].legs[0].steps.size}")

            val polylinePoints = routes[0].overview_polyline.points
            val coordinates = PolyUtil.decode(polylinePoints)
            coordinates.forEach { coordinate ->
                polylineOptions.add(coordinate)
            }
            mMap.addPolyline(polylineOptions)
        }

    }



    private fun fetchDirections(){
        val urlString = "https://maps.googleapis.com/"
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(urlString)
            .build().create(MapApiService::class.java)
        val retrofitData = retrofitBuilder.getDirections(
            "AIzaSyD0IAwuCADIsl-MFp1yhhGWBXxgjlUjbFw",
            "${source.latLng.latitude},${source.latLng.longitude}",
            "${destination.latLng.latitude},${destination.latLng.longitude}","DRIVING")
//        val retrofitData = retrofitBuilder.getDirections(
//            "AIzaSyD0IAwuCADIsl-MFp1yhhGWBXxgjlUjbFw",
//            "Toronto",
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

    private fun fetchNearByPlaces(selLocCoordinates:LatLng) {
        val urlString =  "https://maps.googleapis.com/"
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(urlString)
            .build().create(MapApiService::class.java)
        Log.i(TAG, "responseBody: ${selLocCoordinates.latitude},${selLocCoordinates.longitude}")
//        val retrofitData = retrofitBuilder.getNearByPlaces(
//            "AIzaSyD0IAwuCADIsl-MFp1yhhGWBXxgjlUjbFw",
//            "${selLocCoordinates.latitude},${selLocCoordinates.longitude}",
//            "restaurants", )
//
//        retrofitData.enqueue(object : Callback<GMapApiResponseData?> {
//            override fun onResponse(
//                call: Call<GMapApiResponseData?>,
//                response: Response<GMapApiResponseData?>
//            ) {
//                val responseBody = response.body()!!
//                nearByPlaces = responseBody.results
//                if(nearByPlaces.isNotEmpty()){
//                    displayNearByPlaces = true
//                }
//                onMapReady(mMap)
//            }
//            override fun onFailure(call: Call<GMapApiResponseData?>, t: Throwable) {
//                TODO("Not yet implemented")
//                Log.i(TAG, "Failure Message: ${t.message}")
//            }
//        })
    }
}