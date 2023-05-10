package com.example.planmytrip20.api

import com.google.gson.annotations.SerializedName

data class PexelsResponse(
    @SerializedName("photos")
    val photos: List<PexelsPhoto>
)

data class PexelsPhoto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("url")
    val url: String,

    @SerializedName("photographer")
    val photographer: String,

    @SerializedName("src")
    val src: PexelsPhotoSource
)

data class PexelsPhotoSource(
    @SerializedName("original")
    val original: String,

    @SerializedName("large2x")
    val large2x: String,

    @SerializedName("large")
    val large: String
)
