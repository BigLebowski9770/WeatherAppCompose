package ru.umarsh.weatherappcompose.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.umarsh.weatherappcompose.domain.location.DeviceLocationProvider
import ru.umarsh.weatherappcompose.domain.repository.WeatherRepository
import ru.umarsh.weatherappcompose.feature.weather.WeatherViewModel

class WeatherViewModelFactory(
    private val weatherRepository: WeatherRepository,
    private val locationProvider: DeviceLocationProvider,
    private val useLocation: Boolean,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            return WeatherViewModel(
                weatherRepository = weatherRepository,
                locationProvider = locationProvider,
                useLocation = useLocation,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
