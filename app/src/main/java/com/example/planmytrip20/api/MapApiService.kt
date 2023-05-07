package com.example.planmytrip20.api

import com.example.planmytrip20.classes.GDirectionsAPIResponse
import com.example.planmytrip20.classes.GMapApiResponseData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MapApiService {
    @GET("maps/api/place/nearbysearch/json")
    fun getNearByPlaces(@Query("key") key: String,
                        @Query("location") location: String,
                        @Query("type") type: String,
                        @Query("radius") radius: Int
    ): Call<GMapApiResponseData>

    @GET("/maps/api/directions/json")
    fun getDirections(@Query("key") key: String,
                        @Query("origin") origin: String,
                        @Query("destination") destination: String,
                        @Query("mode") mode: String
    ): Call<GDirectionsAPIResponse>
}
