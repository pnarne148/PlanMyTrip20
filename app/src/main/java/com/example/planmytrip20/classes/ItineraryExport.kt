package com.example.planmytrip20.classes

import java.time.LocalDateTime

data class ItineraryExport(
    var destination: ItineraryLocation?,
    val attraction: List<ItineraryLocation>,
    val recommendations: List<ItineraryLocation>,
    val notes: String?,
    val date: LocalDateTime?
){
    constructor() : this(null, emptyList(), emptyList(), "", null)
}
