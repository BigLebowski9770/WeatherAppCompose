package ru.umarsh.weatherappcompose.feature.weather.mvi

import ru.umarsh.weatherappcompose.domain.model.GeoLocation
import ru.umarsh.weatherappcompose.domain.model.WeatherForecast

data class WeatherState(
    val useLocation: Boolean = false,
    val query: String = "",
    val queryPrefilledFromGps: Boolean = false,
    val searchResults: List<GeoLocation> = emptyList(),
    val forecast: WeatherForecast? = null,
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val error: String? = null,
)

sealed interface WeatherIntent {
    data class QueryChanged(val query: String) : WeatherIntent
    data object SearchFieldFocused : WeatherIntent
    data object Search : WeatherIntent
    data class SelectLocation(val location: GeoLocation) : WeatherIntent
    data object Refresh : WeatherIntent
    data object DismissError : WeatherIntent
}

sealed interface WeatherEffect {
    data class ShowMessage(val message: String) : WeatherEffect
}
