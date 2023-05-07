package com.example.planmytrip20.classes

import com.google.android.gms.maps.model.LatLng

data class ItineraryLocation(
    var location_id: String, // id of location in our database
    var place_id: String, // place id from google
    var name: String,
    var address: String,
    var latLng: LatLng,
    var openingHours: OpeningHours,
    var visited : Boolean = false,
    var description: String,
    val location_image_url: String? = null,
    var rating: Int,
    var wikiUrl: String? = null,
    var user_photo_urls: List<String>? = null
)