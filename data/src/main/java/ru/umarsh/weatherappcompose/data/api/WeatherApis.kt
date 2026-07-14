package ru.umarsh.weatherappcompose.data.api

import retrofit2.http.GET
import retrofit2.http.Query
import ru.umarsh.weatherappcompose.data.dto.ForecastResponseDto
import ru.umarsh.weatherappcompose.data.dto.GeocodingResponseDto

interface GeocodingApi {

    @GET("v1/search")
    suspend fun searchLocations(
        @Query("name") name: String,
        @Query("count") count: Int = 10,
        @Query("language") language: String = "en",
    ): GeocodingResponseDto
}

interface ForecastApi {

    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,weather_code",
        @Query("hourly") hourly: String = "temperature_2m,weather_code",
        @Query("daily") daily: String = "weather_code,temperature_2m_max,temperature_2m_min",
        @Query("timezone") timezone: String = "auto",
        @Query("forecast_days") forecastDays: Int = 7,
    ): ForecastResponseDto
}
