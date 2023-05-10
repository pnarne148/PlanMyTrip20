package com.example.planmytrip20.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

private const val BASE_URL = "https://api.pexels.com/v1/"
private const val API_KEY = "eyZXKeir4DhKc4A2PJdxk8FmdSBbPUPSnB8Hrh1s9bvB7xI0ha4vi1o9"

private val client = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .client(client)
    .build()

interface PexelsAPI {

    @Headers("Authorization: $API_KEY")
    @GET("search")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("per_page") perPage: Int,
        @Query("page") page: Int
    ): PexelsResponse
}

object PexelsApiService {
    val retrofitService: PexelsAPI by lazy {
        retrofit.create(PexelsAPI::class.java)
    }
}
