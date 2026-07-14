package ru.umarsh.weatherappcompose.data.mapper

import ru.umarsh.weatherappcompose.data.dto.CurrentWeatherDto
import ru.umarsh.weatherappcompose.data.dto.DailyWeatherDto
import ru.umarsh.weatherappcompose.data.dto.ForecastResponseDto
import ru.umarsh.weatherappcompose.data.dto.GeocodingResultDto
import ru.umarsh.weatherappcompose.data.dto.HourlyWeatherDto
import ru.umarsh.weatherappcompose.domain.model.CurrentWeather
import ru.umarsh.weatherappcompose.domain.model.DailyWeather
import ru.umarsh.weatherappcompose.domain.model.GeoLocation
import ru.umarsh.weatherappcompose.domain.model.HourlyWeather
import ru.umarsh.weatherappcompose.domain.model.WeatherForecast
import javax.inject.Inject

class WeatherMapper @Inject constructor() {

    fun mapLocation(dto: GeocodingResultDto): GeoLocation {
        return GeoLocation(
            latitude = dto.latitude,
            longitude = dto.longitude,
            name = dto.name,
            country = dto.country,
        )
    }

    fun mapForecast(
        dto: ForecastResponseDto,
        locationName: String,
    ): WeatherForecast {
        return WeatherForecast(
            locationName = locationName,
            latitude = dto.latitude,
            longitude = dto.longitude,
            current = mapCurrent(dto.current),
            hourly = mapHourly(dto.hourly),
            daily = mapDaily(dto.daily),
        )
    }

    private fun mapCurrent(dto: CurrentWeatherDto): CurrentWeather {
        return CurrentWeather(
            time = dto.time,
            temperatureCelsius = dto.temperature2m,
            weatherCode = dto.weatherCode,
        )
    }

    private fun mapHourly(dto: HourlyWeatherDto): List<HourlyWeather> {
        return dto.time.mapIndexed { index, time ->
            HourlyWeather(
                time = time,
                temperatureCelsius = dto.temperature2m[index],
                weatherCode = dto.weatherCode[index],
            )
        }.take(HOURLY_LIMIT)
    }

    private fun mapDaily(dto: DailyWeatherDto): List<DailyWeather> {
        return dto.time.mapIndexed { index, date ->
            DailyWeather(
                date = date,
                weatherCode = dto.weatherCode[index],
                maxTemperatureCelsius = dto.temperature2mMax[index],
                minTemperatureCelsius = dto.temperature2mMin[index],
            )
        }
    }

    private companion object {
        const val HOURLY_LIMIT = 24
    }
}
