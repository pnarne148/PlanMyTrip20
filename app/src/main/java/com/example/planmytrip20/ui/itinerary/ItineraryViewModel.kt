package com.example.planmytrip20.ui.itinerary

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planmytrip20.R
import com.example.planmytrip20.WebScrape.WikipediaApi
import com.example.planmytrip20.api.FirebaseHelper
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

    private val userImages = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRBAHtWe-YxM_NBXquVRo8F5VCpyjC0_LUc4MSEr5PfydzsdUgH-mSQFkHGsV2Mgb75o1S4aLX6Emo&usqp=CAU&ec=48665698",
    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRUiGURvuH253nSLnvvFzNBp8uiA4OqvmKCCUj509afQGQmRCrr_CTTb5ApQM2g3nCfydcUL1bcP1c&usqp=CAU&ec=48665698")

    private val _nextLocationIndex = MutableLiveData<Int>().apply {
        value = 2
    }

    private val _latestIndex = MutableLiveData<Int>().apply {
        value = -1
    }
    val latestIndex : LiveData<Int> = _latestIndex

    private val _prevLocationIndex = MutableLiveData<Int>().apply {
        value = 1
    }

    private val _docReference = MutableLiveData<String>().apply {
        value = ""
    }
    val docReference: LiveData<String> = _docReference

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

    fun setIndex(i:Int)
    {
        _latestIndex.value = i
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
        val selLocCoordinates = selectedLocation.getLatLng()
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
                    ItineraryLocation(index.toString(), result.place_id, result.name, result.name, result.opening_hours, lat, lng,
                        false, desc, imageURL, result.rating, WikipediaApi.getURL(result.name), userImages, bitmap)
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

        val index = recommendedList.indexOfFirst { it.name == recommendedLocation.name && it.getLatLng() == recommendedLocation.getLatLng() }

        if (index != -1) {
            recommendedList.removeAt(index)
        }

        // Add the recommendedLocation to the chosenPlaces list
        if (!chosenList.contains(recommendedLocation)) {
            chosenList.add(recommendedLocation)
        }

        _recommendedPlaces.value = recommendedList
        _chosenPlaces.value = chosenList

        updateFirebaseDB()
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
        updateFirebaseDB()
    }

    fun swapChosenPlaces(pos1: Int, pos2: Int) {
        val placesList = _chosenPlaces.value?.toMutableList() ?: return
        if (pos1 < 0 || pos1 >= placesList.size || pos2 < 0 || pos2 >= placesList.size) {
            return
        }
        Collections.swap(placesList, pos1, pos2)
        _chosenPlaces.value = placesList
        updateFirebaseDB()
    }

    fun addPlace(selLocation: ItineraryLocation){
        viewModelScope.launch {
            val lat = selLocation.latitude
            val lng = selLocation.longitude
            Log.d("itinerery", "onPlaceSelected: "+lat)
            Log.d("itinerery", "onPlaceSelected: "+lng)


            val imageURL = withContext(Dispatchers.IO) {
                selLocation.name?.let { WikipediaApi.getImageUrlFromWikipedia(it) }
            }

            selLocation.description = withContext(Dispatchers.IO) {
                selLocation.name?.let { WikipediaApi.getFirstParagraphFromWikipedia(it) }
            }

            selLocation.bitmap = withContext(Dispatchers.IO) {
                Picasso.get().load(imageURL).resize(100, 100).get()
            }

            selLocation.wikiUrl = selLocation.name?.let { WikipediaApi.getURL(it) }


            val currentList = _chosenPlaces.value?.toMutableList() ?: mutableListOf()
            if (selLocation != null) {
                currentList.add(selLocation)
            }
            _chosenPlaces.value = currentList
            Log.d("itinerery", "onPlaceSelected: "+_chosenPlaces.value?.size)


            updateFirebaseDB()
        }
    }

    fun updateFirebaseDB(){
        val newDestination = destination.value?.copy(bitmap = null)
        val newAttractions = chosenPlaces.value.orEmpty().map { it.copy(bitmap = null) }
        val newRecommendations = recommendedPlaces.value.orEmpty().map { it.copy(bitmap = null) }

        val newItinerary = ItineraryExport(newDestination, newAttractions, newRecommendations)

        if(docReference.value.equals(""))
            FirebaseHelper().createNewItinerary(newItinerary){
                _docReference.value = it
                Log.d("Firebase", "updateFirebaseDB0: "+it)
            }
        else
            FirebaseHelper().updateItinerary(docReference.value.toString(),newItinerary)
        Log.d("firebase", "updateFirebaseDB1: "+docReference.value)
        Log.d("firebase", "updateFirebaseDB2: "+_docReference.value)

//        Log.d("Firebase", "updateFirebaseDB: "+ (task.result.id))
//        _docReference.value = task.result.id
    }

    fun visitPlace(position: Int, checked: Boolean) {
        val updatedChosenPlaces = chosenPlaces.value.orEmpty().toMutableList()
        updatedChosenPlaces[position].visited = checked
        _chosenPlaces.value = updatedChosenPlaces

    }

}