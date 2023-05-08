package com.example.planmytrip20.ui.itinerary

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planmytrip20.R
import com.example.planmytrip20.WebScrape.WikipediaApi
import com.example.planmytrip20.api.MapApiService
import com.example.planmytrip20.classes.GMapApiResponseData
import com.example.planmytrip20.classes.RecommendedLocations
import com.example.planmytrip20.classes.SelectedLocation
import com.google.android.gms.maps.model.LatLng
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    private val _recommendedPlaces = MutableLiveData<List<RecommendedLocations>>()
    val recommendedPlaces: LiveData<List<RecommendedLocations>> = _recommendedPlaces

    private val _chosenPlaces = MutableLiveData<List<RecommendedLocations>>()
    val chosenPlaces: LiveData<List<RecommendedLocations>> = _chosenPlaces

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

        val retrofitData = retrofitBuilder.getNearByPlaces(
            "AIzaSyD0IAwuCADIsl-MFp1yhhGWBXxgjlUjbFw",
            "${selLocCoordinates?.latitude}," +
                    "${selLocCoordinates?.longitude}",
            "tourist_attraction", 15000)

        Log.d("itinerery", "Request URL: ${R.string.maps_api_key.toString()}")

        Log.d("itinerery", "Request URL: ${retrofitData.request().url}")

        retrofitData.enqueue(object : Callback<GMapApiResponseData?> {
            override fun onResponse(
                call: Call<GMapApiResponseData?>,
                response: Response<GMapApiResponseData?>
            ) {
                val responseBody = response.body()!!
                selectedLocation.nearByPlaces = responseBody.results
                Log.d("itinerery", "onCreateView: "+response.body())
                selectedLocation.selection = List(responseBody.results.size) { false }
                initializeLists(selectedLocation)
            }
            override fun onFailure(call: Call<GMapApiResponseData?>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    fun setDestination(place: String?, location: LatLng?) {
        val selLocation = SelectedLocation(place,location, emptyList(), emptyList())
        setDestination(selLocation)
        fetchNearByPlaces(selLocation)
        Log.d("itinerery", "onCreateView: "+selLocation.nearByPlaces.size)
    }

    fun initializeLists(selLocation: SelectedLocation) {
        Log.d("itinerery", "onCreateView:===== ${selLocation.nearByPlaces.size}")

        viewModelScope.launch {
            val recommendedLocations = selLocation.nearByPlaces.mapNotNull { result ->
                val lat = result.geometry?.location?.lat
                val lng = result.geometry?.location?.lng
                if (lat != null && lng != null) {
                    val imageURL = withContext(Dispatchers.IO) {
                        WikipediaApi.getImageUrlFromWikipedia(result.name)
                    }

                    val desc = withContext(Dispatchers.IO) {
                        WikipediaApi.getFirstParagraphFromWikipedia(result.name)
                    }

                    val bitmap = withContext(Dispatchers.IO) {
                        Picasso.get().load(imageURL).resize(100, 100).get()
                    }

                    Log.d("itinerery", "initializeLists: "+imageURL)
                    RecommendedLocations(result.name, LatLng(lat, lng), imageURL, bitmap, desc)

                } else {
                    null
                }
            }
            _recommendedPlaces.value = recommendedLocations.filter { it.bitmap != null }

        }

        _chosenPlaces.value = emptyList()
    }


    fun choosePlace(recommendedLocation: RecommendedLocations) {
        val recommendedList = _recommendedPlaces.value.orEmpty().toMutableList()
        val chosenList = _chosenPlaces.value.orEmpty().toMutableList()

        // Find the index of the recommendedLocation in the recommendedPlaces list
        val index = recommendedList.indexOfFirst { it.name == recommendedLocation.name && it.latLng == recommendedLocation.latLng }

        // Remove the recommendedLocation from the recommendedPlaces list
        if (index != -1) {
            recommendedList.removeAt(index)
        }

        // Add the recommendedLocation to the chosenPlaces list
        if (!chosenList.contains(recommendedLocation)) {
            chosenList.add(recommendedLocation)
        }

        _recommendedPlaces.value = recommendedList
        _chosenPlaces.value = chosenList
    }

    fun unchoosePlace(place: RecommendedLocations) {
        val updatedChosenPlaces = chosenPlaces.value.orEmpty().toMutableList()
        val updatedRecommendedPlaces = recommendedPlaces.value.orEmpty().toMutableList()

        updatedChosenPlaces.remove(place)
        updatedRecommendedPlaces.add(place)

        _chosenPlaces.value = updatedChosenPlaces
        _recommendedPlaces.value = updatedRecommendedPlaces
    }





}