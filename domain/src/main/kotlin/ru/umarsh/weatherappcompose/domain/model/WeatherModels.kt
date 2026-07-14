package ru.umarsh.weatherappcompose.domain.model

data class GeoLocation(
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val country: String? = null,
)

data class WeatherForecast(
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val current: CurrentWeather,
    val hourly: List<HourlyWeather>,
    val daily: List<DailyWeather>,
)

data class CurrentWeather(
    val time: String,
    val temperatureCelsius: Double,
    val weatherCode: Int,
)

data class HourlyWeather(
    val time: String,
    val temperatureCelsius: Double,
    val weatherCode: Int,
)

data class DailyWeather(
    val date: String,
    val weatherCode: Int,
    val maxTemperatureCelsius: Double,
    val minTemperatureCelsius: Double,
)
