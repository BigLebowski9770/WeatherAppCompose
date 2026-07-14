package ru.umarsh.weatherappcompose.domain.repository

import ru.umarsh.weatherappcompose.domain.model.GeoLocation
import ru.umarsh.weatherappcompose.domain.model.WeatherForecast

interface WeatherRepository {
    suspend fun searchLocations(query: String): List<GeoLocation>
    suspend fun getForecast(
        latitude: Double,
        longitude: Double,
        locationName: String? = null,
    ): WeatherForecast
}
