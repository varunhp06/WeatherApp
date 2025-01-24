package com.example.weatherapptest

import android.location.Geocoder
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class Current(
    val temperature_2m: Double,
    val relative_humidity_2m: Int,
    val apparent_temperature: Double,
    val is_day: Int,
    val weather_code: Int
)

@Serializable
data class CurrentResponse(val current: Current)

@Serializable
data class TimeZoneDBResponse(
    val status: String,
    val zoneName: String,
    val timestamp: Long,
    val formatted: String
)

@Serializable
data class CurrentAQI(val us_aqi: Int)

@Serializable
data class CurrentAQIResponse(val current: CurrentAQI)

@Serializable
data class Hourly(
    val temperature_2m: List<Double>,
    val weather_code: List<Int>
)

@Serializable
data class HourlyResponse(
    val hourly: Hourly
)

@Serializable
data class Daily(
    val time: List<String>,
    val weather_code: List<Int>,
    val temperature_2m_max: List<Double>,
    val temperature_2m_min: List<Double>
)

@Serializable
data class WeatherForecast(
    val daily: Daily
)

val client = HttpClient(Android) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        })
    }
}

private val json = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
}

suspend fun getCurrentDetails(latitude: Double?, longitude: Double?): CurrentResponse? = coroutineScope {
    if (latitude == null || longitude == null) {
        Log.e("API Error", "Latitude or Longitude is null")
        return@coroutineScope null
    }

    try {
        val response = async {
            client.get(
                "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&current=temperature_2m,relative_humidity_2m,apparent_temperature,is_day,precipitation,rain,weather_code"
            )
        }.await()

        if (response.status.isSuccess()) {
            val responseBody = response.bodyAsText()
            Log.d("API Response", responseBody)
            json.decodeFromString<CurrentResponse>(responseBody)
        } else {
            Log.e("API Error", "HTTP Error: ${response.status}")
            null
        }
    } catch (e: Exception) {
        Log.e("API Error", "Error fetching location: ${e.message}")
        null
    }
}

suspend fun getTimeZone(latitude: Double?, longitude: Double?): String? = coroutineScope {
    if (latitude == null || longitude == null) {
        Log.e("API Error", "Latitude or Longitude is null")
        return@coroutineScope null
    }

    try {
        val response = async {
            client.get(
                "https://api.timezonedb.com/v2.1/get-time-zone?key=O24X91Q69TI2&format=json&by=position&lat=$latitude&lng=$longitude"
            )
        }.await()

        if (response.status.isSuccess()) {
            val responseBody = response.bodyAsText()
            Log.d("API Response", responseBody)
            json.decodeFromString<TimeZoneDBResponse>(responseBody).zoneName
        } else {
            Log.e("API Error", "HTTP Error: ${response.status}")
            null
        }
    } catch (e: Exception) {
        Log.e("API Error", "Error fetching location: ${e.message}")
        null
    }
}

suspend fun getAQI(latitude: Double?, longitude: Double?): CurrentAQIResponse? = coroutineScope {
    if (latitude == null || longitude == null) {
        Log.e("API Error", "Latitude or Longitude is null")
        return@coroutineScope null
    }

    try {
        val response = async {
            client.get(
                "https://air-quality-api.open-meteo.com/v1/air-quality?latitude=$latitude&longitude=$longitude&current=us_aqi"
            )
        }.await()

        if (response.status.isSuccess()) {
            val responseBody = response.bodyAsText()
            Log.d("API Response", responseBody)
            json.decodeFromString<CurrentAQIResponse>(responseBody)
        } else {
            Log.e("API Error", "HTTP Error: ${response.status}")
            null
        }
    } catch (e: Exception) {
        Log.e("API Error", "Error fetching location: ${e.message}")
        null
    }
}

suspend fun getHourlyDetails(latitude: Double?, longitude: Double?): Hourly? = coroutineScope {
    if (latitude == null || longitude == null) {
        Log.e("API Error", "Latitude or Longitude is null")
        return@coroutineScope null
    }

    try {
        val response = async {
            client.get(
                "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&hourly=temperature_2m,weather_code"
            )
        }.await()

        if (response.status.isSuccess()) {
            val responseBody = response.bodyAsText()
            Log.d("API Response", responseBody)
            json.decodeFromString<HourlyResponse>(responseBody).hourly
        } else {
            Log.e("API Error", "HTTP Error: ${response.status}")
            null
        }
    } catch (e: Exception) {
        Log.e("API Error", "Error fetching location: ${e.message}")
        null
    }
}

suspend fun getWeeklyDetails(latitude: Double?, longitude: Double?): Daily? = coroutineScope {
    if (latitude == null || longitude == null) {
        Log.e("API Error", "Latitude or Longitude is null")
        return@coroutineScope null
    }

    try {
        val response = async {
            client.get(
                "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&daily=weather_code,temperature_2m_max,temperature_2m_min"
            )
        }.await()

        if (response.status.isSuccess()) {
            val responseBody = response.bodyAsText()
            Log.d("API Response", responseBody)
            json.decodeFromString<WeatherForecast>(responseBody).daily
        } else {
            Log.e("API Error", "HTTP Error: ${response.status}")
            null
        }
    } catch (e: Exception) {
        Log.e("API Error", "Error fetching location: ${e.message}")
        null
    }
}

@Composable
fun getCityName(lat: Double?, lon: Double?): String? {
    if (lat != null && lon != null) {
        val context = LocalContext.current
        val geocoder = Geocoder(context, java.util.Locale.getDefault())
        val addresses = geocoder.getFromLocation(lat, lon, 1)
        return addresses?.firstOrNull()?.locality
    }
    return ""
}

fun getCurrentDateAndTime(timeZone: String): String {
    val zonedDateTime = ZonedDateTime.now(ZoneId.of(timeZone))
    val formatter = DateTimeFormatter.ofPattern("MMMM d,\n\nEEEE")
    return zonedDateTime.format(formatter)
}