package com.example.planmytrip20.classes

import com.google.android.gms.maps.model.LatLng
import com.google.type.DateTime

data class Itinerary(
    val name: String,
    val description: String,
    val address: String,
    val latLng: LatLng,
    val locations: List<ItineraryLocation>,
    val itinerary_id: String,
    val start_date: DateTime,
    val end_date: DateTime,
    val place_id: String,
    val isOptimized: Boolean = false,
    val isCompleted: Boolean = false,
    val user_id: String
)
