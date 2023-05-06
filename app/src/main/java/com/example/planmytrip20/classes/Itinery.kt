package com.example.planmytrip20.classes

import com.google.android.gms.maps.model.LatLng

class Itinery(arrangedList: List<SelectedLocation>) {

    var places: List<SelectedLocation> = arrangedList
    var activities: List<ThingsToDO> = emptyList()

    fun addPlace(place: SelectedLocation) {
        places = places.plus(place)
    }

    fun removePlace(place: SelectedLocation) {
        places = places.minus(place)
    }

    fun addActivity(thingsToDO: ThingsToDO) {
        activities = activities.plus(thingsToDO)
    }

    fun removeActivity(thingsToDO: ThingsToDO) {
        activities = activities.minus(thingsToDO)
    }

//    fun getDistance(): Double {
//        return activities.fold(0.0) { acc, activity -> acc + activity.distance }
//    }
//
//    fun getTimeSpent(): Int {
//        return activities.fold(0) { acc, activity -> acc + activity.timeSpent }
//    }

    fun getSelectedLocationsNames(): List<String> {
        return places.map { it.address }
    }

    fun getSelectedLocationsAddresses(): List<String> {
        return places.map { it.address }
    }

    fun getSelectedLocationsLatLng(): List<LatLng> {
        return places.map { it.latLng }
    }
}
