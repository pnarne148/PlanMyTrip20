package com.example.planmytrip20.classes

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng

data class RecommendedLocations(
    val name: String,
    val latLng: LatLng?,
    val imageURL: String?,
    val bitmap: Bitmap?,
    val desc: String?
)