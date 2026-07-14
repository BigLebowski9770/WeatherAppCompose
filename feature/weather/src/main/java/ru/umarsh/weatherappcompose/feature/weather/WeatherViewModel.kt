package ru.umarsh.weatherappcompose.feature.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.umarsh.weatherappcompose.domain.location.DeviceLocationProvider
import ru.umarsh.weatherappcompose.domain.model.GeoLocation
import ru.umarsh.weatherappcompose.domain.repository.WeatherRepository
import ru.umarsh.weatherappcompose.feature.weather.mvi.WeatherEffect
import ru.umarsh.weatherappcompose.feature.weather.mvi.WeatherIntent
import ru.umarsh.weatherappcompose.feature.weather.mvi.WeatherState

class WeatherViewModel(
    private val weatherRepository: WeatherRepository,
    private val locationProvider: DeviceLocationProvider,
    useLocation: Boolean,
) : ViewModel() {

    private val _state = MutableStateFlow(WeatherState(useLocation = useLocation))
    val state: StateFlow<WeatherState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<WeatherEffect>()
    val effect: SharedFlow<WeatherEffect> = _effect.asSharedFlow()

    init {
        if (useLocation) {
            loadFromGps()
        }
    }

    fun onIntent(intent: WeatherIntent) {
        when (intent) {
            is WeatherIntent.QueryChanged -> {
                _state.update {
                    it.copy(
                        query = intent.query,
                        queryPrefilledFromGps = false,
                        error = null,
                    )
                }
            }

            WeatherIntent.SearchFieldFocused -> {
                if (_state.value.queryPrefilledFromGps) {
                    _state.update {
                        it.copy(
                            query = "",
                            queryPrefilledFromGps = false,
                        )
                    }
                }
            }

            WeatherIntent.Search -> searchCity()

            is WeatherIntent.SelectLocation -> loadForecast(intent.location)

            WeatherIntent.Refresh -> refresh()

            WeatherIntent.DismissError -> {
                _state.update { it.copy(error = null) }
            }
        }
    }

    private fun refresh() {
        val forecast = _state.value.forecast
        if (forecast != null) {
            loadForecast(
                GeoLocation(
                    latitude = forecast.latitude,
                    longitude = forecast.longitude,
                    name = forecast.locationName,
                ),
            )
            return
        }

        if (_state.value.useLocation) {
            loadFromGps()
        }
    }

    private fun searchCity() {
        val query = _state.value.query.trim()
        if (query.isBlank()) {
            viewModelScope.launch {
                _effect.emit(WeatherEffect.ShowMessage(EMPTY_QUERY_MESSAGE))
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSearching = true, error = null, searchResults = emptyList()) }
            runCatching {
                weatherRepository.searchLocations(query)
            }.onSuccess { locations ->
                _state.update {
                    it.copy(
                        isSearching = false,
                        searchResults = locations,
                        error = if (locations.isEmpty()) "No cities found" else null,
                    )
                }
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isSearching = false,
                        error = error.message ?: "Search failed",
                    )
                }
            }
        }
    }

    private fun loadFromGps() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            runCatching {
                val location = locationProvider.getCurrentLocation()
                    ?: error("Location unavailable. Try search instead.")
                weatherRepository.getForecast(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    locationName = location.name,
                )
            }.onSuccess { forecast ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        forecast = forecast,
                        searchResults = emptyList(),
                        query = forecast.locationName,
                        queryPrefilledFromGps = true,
                        error = null,
                    )
                }
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load weather",
                    )
                }
            }
        }
    }

    private fun loadForecast(location: GeoLocation) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    searchResults = emptyList(),
                    query = location.name,
                    queryPrefilledFromGps = false,
                )
            }
            runCatching {
                weatherRepository.getForecast(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    locationName = location.displayName(),
                )
            }.onSuccess { forecast ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        forecast = forecast,
                        error = null,
                    )
                }
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = error.message ?: "Failed to load weather",
                    )
                }
            }
        }
    }

    private fun GeoLocation.displayName(): String {
        return if (country.isNullOrBlank()) name else "$name, $country"
    }

    private companion object {
        const val EMPTY_QUERY_MESSAGE = "Enter city name"
    }
}
