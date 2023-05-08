package com.example.planmytrip20.ui.itinerary

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planmytrip20.R
import com.example.planmytrip20.WebScrape.WikipediaApi
import com.example.planmytrip20.api.MapApiService
import com.example.planmytrip20.classes.*
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
import java.util.Collections

class ItineraryViewModel : ViewModel() {

    private val _nextLocationIndex = MutableLiveData<Int>().apply {
        value = 2
    }

    private val _prevLocationIndex = MutableLiveData<Int>().apply {
        value = 1
    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is itinerary Fragment"
    }
    val text: LiveData<String> = _text
    val nextLocationIndex: LiveData<Int> = _nextLocationIndex
    val prevLocationIndex: LiveData<Int> = _prevLocationIndex

    private val _notes = MutableLiveData<String>().apply {
        value = ""
    }
    val notes: LiveData<String> = _notes

    private val _destination = MutableLiveData<ItineraryLocation>().apply {
        value = null
    }
    val destination: LiveData<ItineraryLocation> = _destination

    private val _recommendedPlaces = MutableLiveData<List<ItineraryLocation>>()
    val recommendedPlaces: LiveData<List<ItineraryLocation>> = _recommendedPlaces

    private val _chosenPlaces = MutableLiveData<List<ItineraryLocation>>()
    val chosenPlaces: LiveData<List<ItineraryLocation>> = _chosenPlaces

    var nearbyPlaces: List<com.example.planmytrip20.classes.Result> = emptyList()

    fun setText(str:String)
    {
        _text.value = str
    }

    fun setNextLocationIndex(value:Int)
    {
        _nextLocationIndex.value = value
    }

    fun setPrevLocationIndex(value:Int) {
        _prevLocationIndex.value = value
    }

    fun setNotes(str:String)
    {
        _notes.value = str
    }

    fun setDestination(loc : ItineraryLocation?)
    {
        _destination.value = loc
        if (loc != null) {
            fetchNearByPlaces(loc)
        }
    }

    fun getNearbyPlaces()
    {
        _destination.value?.let { fetchNearByPlaces(it) }
    }

    fun fetchNearByPlaces(selectedLocation: ItineraryLocation) {
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
                nearbyPlaces = responseBody.results
                Log.d("itinerery", "onCreateView: "+response.body())
                initializeLists(nearbyPlaces)
            }
            override fun onFailure(call: Call<GMapApiResponseData?>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }


    fun initializeLists(nearbyPlaces: List<com.example.planmytrip20.classes.Result>) {
        Log.d("itinerery", "onCreateView:===== ${nearbyPlaces.size}")

        viewModelScope.launch {
            val recommendedLocations = nearbyPlaces.mapIndexedNotNull { index, result ->
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
                    ItineraryLocation(index.toString(), result.place_id, result.name, result.name, LatLng(lat, lng),
                        result.opening_hours, false, desc, imageURL, result.rating, WikipediaApi.getURL(result.name), null, bitmap)
                } else {
                    null
                }
            }
            _recommendedPlaces.value = recommendedLocations.filter { it.bitmap != null }

        }

        _chosenPlaces.value = emptyList()
    }


    fun choosePlace(recommendedLocation: ItineraryLocation) {
        val recommendedList = _recommendedPlaces.value.orEmpty().toMutableList()
        val chosenList = _chosenPlaces.value.orEmpty().toMutableList()

        val index = recommendedList.indexOfFirst { it.name == recommendedLocation.name && it.latLng == recommendedLocation.latLng }

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

    fun unchoosePlace(place: ItineraryLocation) {
        val updatedChosenPlaces = chosenPlaces.value.orEmpty().toMutableList()
        val updatedRecommendedPlaces = recommendedPlaces.value.orEmpty().toMutableList()

        updatedChosenPlaces.remove(place)
        updatedRecommendedPlaces.add(place)

        _chosenPlaces.value = updatedChosenPlaces
        _recommendedPlaces.value = updatedRecommendedPlaces
    }

    fun unchoosePlace(index: Int) {
        val updatedChosenPlaces = chosenPlaces.value.orEmpty().toMutableList()
        val updatedRecommendedPlaces = recommendedPlaces.value.orEmpty().toMutableList()

        val place = updatedChosenPlaces[index]
        updatedChosenPlaces.remove(place)
        updatedRecommendedPlaces.add(place)

        _chosenPlaces.value = updatedChosenPlaces
        _recommendedPlaces.value = updatedRecommendedPlaces
    }

    fun swapChosenPlaces(pos1: Int, pos2: Int) {
        val placesList = _chosenPlaces.value?.toMutableList() ?: return
        if (pos1 < 0 || pos1 >= placesList.size || pos2 < 0 || pos2 >= placesList.size) {
            return
        }
        Collections.swap(placesList, pos1, pos2)
        _chosenPlaces.value = placesList
    }

    fun addPlace(selLocation: SelectedLocation){
        viewModelScope.launch {
            val lat = selLocation.latLng?.latitude
            val lng = selLocation.latLng?.longitude
            Log.d("itinerery", "onPlaceSelected: "+lat)
            Log.d("itinerery", "onPlaceSelected: "+lng)

            if (lat != null && lng != null) {
                val imageURL = withContext(Dispatchers.IO) {
                    selLocation.address?.let { WikipediaApi.getImageUrlFromWikipedia(it) }
                }

                val desc = withContext(Dispatchers.IO) {
                    selLocation.address?.let { WikipediaApi.getFirstParagraphFromWikipedia(it) }
                }

                val bitmap = withContext(Dispatchers.IO) {
                    Picasso.get().load(imageURL).resize(100, 100).get()
                }

                Log.d("itinerery", "initializeLists: "+imageURL)
//                val reccLocation = selLocation.address?.let {
//                    ItineraryLocation(_recommendedPlaces.value?.size.toString(),
//
//                        it, LatLng(lat, lng), imageURL, bitmap, desc)
//                }

                var reccLocation =  (ItineraryLocation("test_id", "test_place_id", "Brighton MA 1",
                    "50 Winship Street", LatLng(42.9912, -70.456), OpeningHours(true), true, "The contents within a card should follow their own accessibility guidelines, such as images having content descriptions set on them.", rating = 5.0))


                Log.d("itinerery", "onPlaceSelected: "+_chosenPlaces.value?.size)

                val currentList = _chosenPlaces.value?.toMutableList() ?: mutableListOf()
                if (reccLocation != null) {
                    currentList.add(reccLocation)
                }
                _chosenPlaces.value = currentList
                Log.d("itinerery", "onPlaceSelected: "+_chosenPlaces.value?.size)


            } else {
                null
            }

        }
    }

}