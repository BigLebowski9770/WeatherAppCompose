package ru.umarsh.weatherappcompose.data.mapper

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.umarsh.weatherappcompose.data.dto.CurrentWeatherDto
import ru.umarsh.weatherappcompose.data.dto.DailyWeatherDto
import ru.umarsh.weatherappcompose.data.dto.ForecastResponseDto
import ru.umarsh.weatherappcompose.data.dto.GeocodingResultDto
import ru.umarsh.weatherappcompose.data.dto.HourlyWeatherDto

class WeatherMapperTest {

    private val mapper = WeatherMapper()

    @Test
    fun mapLocation_mapsFields() {
        val location = mapper.mapLocation(
            GeocodingResultDto(
                name = "Almaty",
                latitude = 43.25,
                longitude = 76.91,
                country = "Kazakhstan",
            ),
        )

        assertEquals("Almaty", location.name)
        assertEquals(43.25, location.latitude, 0.0)
        assertEquals(76.91, location.longitude, 0.0)
        assertEquals("Kazakhstan", location.country)
    }

    @Test
    fun mapForecast_limitsHourlyToTwentyFourItems() {
        val hourlyTimes = (1..30).map { "2026-07-14T${it.toString().padStart(2, '0')}:00" }
        val hourlyTemperatures = (1..30).map { it.toDouble() }
        val hourlyCodes = (1..30).map { 0 }

        val forecast = mapper.mapForecast(
            ForecastResponseDto(
                latitude = 43.25,
                longitude = 76.91,
                current = CurrentWeatherDto(
                    time = "2026-07-14T12:00",
                    temperature2m = 25.0,
                    weatherCode = 1,
                ),
                hourly = HourlyWeatherDto(
                    time = hourlyTimes,
                    temperature2m = hourlyTemperatures,
                    weatherCode = hourlyCodes,
                ),
                daily = DailyWeatherDto(
                    time = listOf("2026-07-14"),
                    weatherCode = listOf(1),
                    temperature2mMax = listOf(30.0),
                    temperature2mMin = listOf(20.0),
                ),
            ),
            locationName = "Almaty",
        )

        assertEquals("Almaty", forecast.locationName)
        assertEquals(24, forecast.hourly.size)
        assertEquals(1, forecast.daily.size)
        assertEquals(25.0, forecast.current.temperatureCelsius, 0.0)
    }
}
