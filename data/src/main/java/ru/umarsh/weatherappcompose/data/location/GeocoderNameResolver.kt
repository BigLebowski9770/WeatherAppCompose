package ru.umarsh.weatherappcompose.data.location

import android.location.Address
import android.location.Geocoder
import android.os.Build
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

internal class GeocoderNameResolver(
    private val geocoder: Geocoder,
) {

    suspend fun resolveLocationName(latitude: Double, longitude: Double): String {
        if (!Geocoder.isPresent()) {
            return formatCoordinates(latitude, longitude)
        }

        val address = runCatching {
            getAddress(latitude, longitude)
        }.getOrNull()

        return address?.toLocationName() ?: formatCoordinates(latitude, longitude)
    }

    private suspend fun getAddress(latitude: Double, longitude: Double): Address? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return suspendCancellableCoroutine { continuation ->
                geocoder.getFromLocation(
                    latitude,
                    longitude,
                    1,
                    object : Geocoder.GeocodeListener {
                        override fun onGeocode(addresses: MutableList<Address>) {
                            if (continuation.isActive) {
                                continuation.resume(addresses.firstOrNull())
                            }
                        }

                        override fun onError(errorMessage: String?) {
                            if (continuation.isActive) {
                                continuation.resume(null)
                            }
                        }
                    },
                )
            }
        }

        @Suppress("DEPRECATION")
        return geocoder.getFromLocation(latitude, longitude, 1)?.firstOrNull()
    }

    private fun Address.toLocationName(): String {
        val city = locality
            ?: subAdminArea
            ?: adminArea
            ?: subLocality
            ?: thoroughfare

        val country = countryName

        return when {
            city != null && country != null -> "$city, $country"
            city != null -> city
            country != null -> country
            else -> formatCoordinates(latitude, longitude)
        }
    }

    private fun formatCoordinates(latitude: Double, longitude: Double): String {
        return String.format(Locale.US, "%.2f°, %.2f°", latitude, longitude)
    }
}
