package com.example.planmytrip20.classes

data class GMapApiResponseData(
    val html_attributions: List<Any>,
    val results: List<Result>,
    val status: String
)