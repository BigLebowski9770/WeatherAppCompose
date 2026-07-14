package ru.umarsh.weatherappcompose.data.dto

import com.google.gson.annotations.SerializedName

data class GeocodingResponseDto(
    val results: List<GeocodingResultDto>?,
)

data class GeocodingResultDto(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String?,
)

data class ForecastResponseDto(
    val latitude: Double,
    val longitude: Double,
    val current: CurrentWeatherDto,
    val hourly: HourlyWeatherDto,
    val daily: DailyWeatherDto,
)

data class CurrentWeatherDto(
    val time: String,
    @SerializedName("temperature_2m")
    val temperature2m: Double,
    @SerializedName("weather_code")
    val weatherCode: Int,
)

data class HourlyWeatherDto(
    val time: List<String>,
    @SerializedName("temperature_2m")
    val temperature2m: List<Double>,
    @SerializedName("weather_code")
    val weatherCode: List<Int>,
)

data class DailyWeatherDto(
    val time: List<String>,
    @SerializedName("weather_code")
    val weatherCode: List<Int>,
    @SerializedName("temperature_2m_max")
    val temperature2mMax: List<Double>,
    @SerializedName("temperature_2m_min")
    val temperature2mMin: List<Double>,
)
