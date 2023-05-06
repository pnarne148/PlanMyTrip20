package com.example.planmytrip20.classes

import com.google.android.gms.maps.model.LatLng

class ThingsToDO(map: List<String>, map1: List<LatLng>) {
    public var place: List<String> = map
    public var latLng: List<LatLng> = map1
    public lateinit var distance : List<Double>
    public lateinit var timeSpent: List<Int>
}