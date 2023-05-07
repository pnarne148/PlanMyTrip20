package com.example.planmytrip20.ui.itinerary

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.planmytrip20.BuildConfig
import com.example.planmytrip20.R
import com.example.planmytrip20.api.MapApiService
import com.example.planmytrip20.classes.GMapApiResponseData
import com.example.planmytrip20.classes.SelectedLocation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ItineraryViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is itinerary Fragment"
    }
    val text: LiveData<String> = _text

    private val _notes = MutableLiveData<String>().apply {
        value = ""
    }
    val notes: LiveData<String> = _notes


    private val _destination = MutableLiveData<SelectedLocation>().apply {
        value = SelectedLocation("",null, emptyList(), emptyList())
    }
    val destination: LiveData<SelectedLocation> = _destination


    fun setText(str:String)
    {
        _text.value = str
    }

    fun setNotes(str:String)
    {
        _notes.value = str
    }

    fun setDestination(loc : SelectedLocation?)
    {
        _destination.value = loc
    }

    fun getNearbyPlaces()
    {
        _destination.value?.let { fetchNearByPlaces(it) }
    }

    private fun fetchNearByPlaces(selectedLocation: SelectedLocation) {
        val selLocCoordinates = selectedLocation.latLng
        val urlString = "https://maps.googleapis.com/"
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(urlString)
            .build().create(MapApiService::class.java)
//        Log.i(TAG, "responseBody: ${selLocCoordinates.latitude},${selLocCoordinates.longitude}")
        val retrofitData = retrofitBuilder.getNearByPlaces(
            R.string.maps_api_key.toString(),
            "${selLocCoordinates?.latitude},${selLocCoordinates?.longitude}",
            "tourist_attraction", 15000)

        retrofitData.enqueue(object : Callback<GMapApiResponseData?> {
            override fun onResponse(
                call: Call<GMapApiResponseData?>,
                response: Response<GMapApiResponseData?>
            ) {
                val responseBody = response.body()!!
                selectedLocation.nearByPlaces = responseBody.results
                selectedLocation.selection = List(responseBody.results.size) { false }
//                Log.i(TAG, "responseBody: $responseBody")
            }
            override fun onFailure(call: Call<GMapApiResponseData?>, t: Throwable) {
                TODO("Not yet implemented")
//                Log.i(TAG, "Failure Message: ${t.message}")
            }
        })
    }


}