package ru.umarsh.weatherappcompose.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.LocationManager
import java.util.Locale
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.umarsh.weatherappcompose.core.common.DispatcherProvider
import ru.umarsh.weatherappcompose.domain.location.DeviceLocationProvider
import ru.umarsh.weatherappcompose.domain.model.GeoLocation
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidLocationProvider @Inject constructor(
    private val context: Context,
    dispatcherProvider: DispatcherProvider,
) : DeviceLocationProvider {

    private val ioDispatcher: CoroutineDispatcher = dispatcherProvider.io
    private val geocoderNameResolver by lazy {
        GeocoderNameResolver(Geocoder(context, Locale.getDefault()))
    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): GeoLocation? = withContext(ioDispatcher) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        location?.let {
            val locationName = geocoderNameResolver.resolveLocationName(
                latitude = it.latitude,
                longitude = it.longitude,
            )
            GeoLocation(
                latitude = it.latitude,
                longitude = it.longitude,
                name = locationName,
                country = null,
            )
        }
    }
}
