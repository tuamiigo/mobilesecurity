package be.mobilesecurity.network

import okhttp3.*
import java.io.IOException
import android.util.Log
import org.json.JSONObject

class AppointmentApiService {
    private val client = OkHttpClient()

    fun fetchLocationDetails(locationName: String, successCallback: (Double, Double, String) -> Unit) {

        val encodedLocationName = java.net.URLEncoder.encode(locationName, "UTF-8")
        val url = "https://geocoding-api.open-meteo.com/v1/search?name=$encodedLocationName&language=en&format=json&count=1"

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("AppointmentApiService", "API call failed", e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        Log.e("AppointmentApiService", "Unexpected response code $response")
                        return
                    }

                    val responseData = response.body?.string()
                    responseData?.let {
                        try {
                            val jsonObject = JSONObject(it)
                            val results = jsonObject.getJSONArray("results")
                            if (results.length() > 0) {
                                val location = results.getJSONObject(0)
                                val latitude = location.getDouble("latitude")
                                val longitude = location.getDouble("longitude")
                                val countryCode = location.getString("country_code")

                                successCallback(latitude, longitude, countryCode)
                                Log.e("AppointmentApiService", "Response successful")
                                Log.e("AppointmentApiService", "Longitude: $longitude")
                                Log.e("AppointmentApiService", "Latitude: $latitude")
                                Log.e("AppointmentApiService", "Country Code: $countryCode")
                            } else {
                                Log.e("AppointmentApiService", "No results found")
                            }
                        } catch (e: Exception) {
                            Log.e("AppointmentApiService", "Error parsing JSON response", e)
                        }
                    } ?: run {
                        Log.e("AppointmentApiService", "Received null response body")
                    }
                }
            }
        })
    }
}