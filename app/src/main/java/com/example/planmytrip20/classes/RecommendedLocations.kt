package com.example.planmytrip20.classes

import com.google.android.gms.maps.model.LatLng

data class RecommendedLocations (
    val name: String,
    val latLng: LatLng?
)