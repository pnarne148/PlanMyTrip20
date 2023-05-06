package com.example.planmytrip20.classes

import com.google.android.gms.maps.model.LatLng

data class SelectedLocation(
    val address: String,
    val latLng: LatLng,
    var nearByPlaces: List<Result>,
    var selection: List<Boolean>
)