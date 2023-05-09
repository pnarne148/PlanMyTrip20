package com.example.planmytrip20.classes

data class ItineraryExport(
    var destination: ItineraryLocation?,
    val attraction: List<ItineraryLocation>,
    val recommendations: List<ItineraryLocation>
){
    constructor() : this(null, emptyList(), emptyList())
}
