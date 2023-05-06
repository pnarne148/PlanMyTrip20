package com.example.planmytrip20.api

import com.example.planmytrip20.classes.GMapApiResponseData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface FirebaseApiService {

    @GET("itineraries.json")
    fun getAllUserItineraries(
        @Query("user_id") key: String
    ): Call<GMapApiResponseData>

}