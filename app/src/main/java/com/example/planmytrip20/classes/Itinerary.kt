package com.example.planmytrip20.classes

import com.google.android.gms.maps.model.LatLng
import com.google.type.DateTime

data class Itinerary(
    var name: String,
    var description: String,
    var address: String,
    var latLng: LatLng,
    var locations: List<ItineraryLocation>,
    var itinerary_id: String,
    var start_date: DateTime,
    var end_date: DateTime,
    var place_id: String,
    var isOptimized: Boolean = false,
    var isCompleted: Boolean = false,
    var user_id: String,
    var nextLocationIndex: Int = 1,
    var prevLocationIndex: Int = 0,
    var isTravelling: Boolean = false,
    var wikiUrl: String? = null,
    var location_image_url: String? = null,
    var user_photo_urls: List<String>? = null
)
