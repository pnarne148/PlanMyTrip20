package com.example.planmytrip20.classes

import com.google.android.gms.maps.model.LatLng

data class ItineraryLocation(
    val location_id: String, // id of location in our database
    val place_id: String, // place id from google
    val name: String,
    val address: String,
    val latLng: LatLng,
    val openingHours: OpeningHours,
    val visited : Boolean = false,
    val description: String,
    val location_image: Int? = null,
    val rating: Int
)