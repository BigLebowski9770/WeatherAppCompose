package ru.umarsh.weatherappcompose.data.repository

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.umarsh.weatherappcompose.core.common.DispatcherProvider
import ru.umarsh.weatherappcompose.data.api.ForecastApi
import ru.umarsh.weatherappcompose.data.api.GeocodingApi
import ru.umarsh.weatherappcompose.data.dto.ForecastResponseDto
import ru.umarsh.weatherappcompose.data.local.ForecastCacheEntity
import ru.umarsh.weatherappcompose.data.local.ForecastCacheDao
import ru.umarsh.weatherappcompose.data.mapper.WeatherMapper
import ru.umarsh.weatherappcompose.domain.model.GeoLocation
import ru.umarsh.weatherappcompose.domain.model.WeatherForecast
import ru.umarsh.weatherappcompose.domain.repository.WeatherRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val geocodingApi: GeocodingApi,
    private val forecastApi: ForecastApi,
    private val cacheDao: ForecastCacheDao,
    private val mapper: WeatherMapper,
    private val gson: Gson,
    dispatcherProvider: DispatcherProvider,
) : WeatherRepository {

    private val ioDispatcher: CoroutineDispatcher = dispatcherProvider.io

    override suspend fun searchLocations(query: String): List<GeoLocation> = withContext(ioDispatcher) {
        if (query.isBlank()) {
            Log.d(LOG_TAG, "searchLocations: blank query")
            return@withContext emptyList()
        }

        Log.d(LOG_TAG, "searchLocations: query=$query")
        val locations = geocodingApi.searchLocations(name = query.trim())
            .results
            .orEmpty()
            .map(mapper::mapLocation)
        Log.d(LOG_TAG, "searchLocations: results=${locations.size}")
        locations
    }

    override suspend fun getForecast(
        latitude: Double,
        longitude: Double,
        locationName: String?,
    ): WeatherForecast = withContext(ioDispatcher) {
        val resolvedName = locationName ?: readCachedLocationName(latitude, longitude)
        Log.d(LOG_TAG, "getForecast: lat=$latitude, lon=$longitude, name=$resolvedName")

        try {
            val response = forecastApi.getForecast(
                latitude = latitude,
                longitude = longitude,
            )
            val forecast = mapper.mapForecast(response, resolvedName)
            cacheForecast(response, forecast)
            Log.d(
                LOG_TAG,
                "getForecast: network OK, temp=${forecast.current.temperatureCelsius}, cached",
            )
            forecast
        } catch (error: Exception) {
            Log.w(LOG_TAG, "getForecast: network failed, trying cache", error)
            readCachedForecast(latitude, longitude)?.also {
                Log.d(LOG_TAG, "getForecast: cache HIT for ${it.locationName}")
            } ?: run {
                Log.e(LOG_TAG, "getForecast: cache MISS")
                throw error
            }
        }
    }

    private suspend fun readCachedLocationName(latitude: Double, longitude: Double): String {
        val cache = cacheDao.get() ?: return DEFAULT_LOCATION_NAME
        return if (cache.matchesCoordinates(latitude, longitude)) {
            cache.locationName
        } else {
            DEFAULT_LOCATION_NAME
        }
    }

    private suspend fun readCachedForecast(latitude: Double, longitude: Double): WeatherForecast? {
        val cache = cacheDao.get() ?: return null
        if (!cache.matchesCoordinates(latitude, longitude)) {
            return null
        }

        val dto = gson.fromJson(cache.responseJson, ForecastResponseDto::class.java)
        return mapper.mapForecast(dto, cache.locationName)
    }

    private suspend fun cacheForecast(
        response: ForecastResponseDto,
        forecast: WeatherForecast,
    ) {
        cacheDao.insert(
            ForecastCacheEntity(
                latitude = forecast.latitude,
                longitude = forecast.longitude,
                locationName = forecast.locationName,
                responseJson = gson.toJson(response),
                updatedAtMillis = System.currentTimeMillis(),
            ),
        )
    }

    private fun ForecastCacheEntity.matchesCoordinates(latitude: Double, longitude: Double): Boolean {
        return latitude.isSameCoordinate(this.latitude) && longitude.isSameCoordinate(this.longitude)
    }

    private fun Double.isSameCoordinate(other: Double): Boolean = abs(this - other) < COORDINATE_EPSILON

    private companion object {
        const val LOG_TAG = "WeatherRepo"
        const val DEFAULT_LOCATION_NAME = "Current location"
        const val COORDINATE_EPSILON = 0.01
    }
}
