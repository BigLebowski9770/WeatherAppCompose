package ru.umarsh.weatherappcompose.domain.location

import ru.umarsh.weatherappcompose.domain.model.GeoLocation

interface DeviceLocationProvider {
    suspend fun getCurrentLocation(): GeoLocation?
}
