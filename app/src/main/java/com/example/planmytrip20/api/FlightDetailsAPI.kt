package com.example.planmytrip20.api

import android.util.Log
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class FlightDetailsFetcher(private val listener: FlightDetailsListener) {

    private val client = OkHttpClient()

    interface FlightDetailsListener {
        fun onFlightDetailsSuccess(flightDetails: FlightDetails)
        fun onFlightDetailsError(errorMessage: String)
    }

    fun fetchFlightDetails(flightNumber: String, date: String, airlineCode: String) {
        val appId = "91fccc10"
        val appKey = "aa8764e72dbc249ae04b2645ea94b11d"

        val url = HttpUrl.Builder()
            .scheme("https")
            .host("api.flightstats.com")
            .addPathSegment("flex")
            .addPathSegment("flightstatus")
            .addPathSegment("v2")
            .addPathSegment("json")
            .addPathSegment("flight")
            .addPathSegment(airlineCode)
            .addPathSegment(flightNumber)
            .addPathSegment("arr")
            .addPathSegment(date)
            .addQueryParameter("appId", appId)
            .addQueryParameter("appKey", appKey)
            .build()

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                listener.onFlightDetailsError("Failed to fetch flight details: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val jsonResponse = JSONObject(responseBody.string())

                    // Parse the response JSON and extract the flight details
                    val flightDetails = FlightDetails.parseFromJson(responseBody.string())

                    listener.onFlightDetailsSuccess(flightDetails)
                } ?: run {
                    listener.onFlightDetailsError("Failed to fetch flight details")
                }
            }
        })
    }

}


class FlightDetails(
    val flightNumber: String,
    val date: String,
    val airline: String,
    val departureAirport: String,
    val arrivalAirport: String
) {
    companion object {
        fun parseFromJson(jsonString: String): FlightDetails {
            val jsonObject = JSONObject(jsonString)
            val flightNumber = jsonObject.getString("flight_number")
            val date = jsonObject.getString("date")
            val airline = jsonObject.getString("airline")
            val departureAirport = jsonObject.getString("departure_airport")
            val arrivalAirport = jsonObject.getString("arrival_airport")
            return FlightDetails(flightNumber, date, airline, departureAirport, arrivalAirport)
        }
    }
}
