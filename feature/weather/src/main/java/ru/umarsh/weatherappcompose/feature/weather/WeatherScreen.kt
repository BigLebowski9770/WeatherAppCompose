package ru.umarsh.weatherappcompose.feature.weather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.umarsh.weatherappcompose.domain.model.GeoLocation
import ru.umarsh.weatherappcompose.domain.model.WeatherForecast
import ru.umarsh.weatherappcompose.feature.weather.mvi.WeatherEffect
import ru.umarsh.weatherappcompose.feature.weather.mvi.WeatherIntent
import ru.umarsh.weatherappcompose.feature.weather.mvi.WeatherState
import ru.umarsh.weatherappcompose.feature.weather.ui.WeatherCodeIcon
import ru.umarsh.weatherappcompose.feature.weather.ui.formatDay
import ru.umarsh.weatherappcompose.feature.weather.ui.formatHour
import ru.umarsh.weatherappcompose.feature.weather.ui.formatTemperature
import ru.umarsh.weatherappcompose.feature.weather.ui.weatherCodeDescription

@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarMutex = remember { Mutex() }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is WeatherEffect.ShowMessage -> snackbarMutex.withLock {
                    val visibleMessage = snackbarHostState.currentSnackbarData?.visuals?.message
                    if (visibleMessage == effect.message) return@withLock
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        WeatherContent(
            state = state,
            onIntent = viewModel::onIntent,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        )
    }
}

@Composable
private fun WeatherContent(
    state: WeatherState,
    onIntent: (WeatherIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    if (state.isLoading && state.forecast == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    PullToRefreshBox(
        isRefreshing = state.isLoading && state.forecast != null,
        onRefresh = { onIntent(WeatherIntent.Refresh) },
        modifier = modifier,
    ) {
        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
        ) {
            item {
                SearchSection(
                    state = state,
                    onSearch = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        onIntent(WeatherIntent.Search)
                    },
                    onQueryChange = { onIntent(WeatherIntent.QueryChanged(it)) },
                    onSearchFieldFocused = { onIntent(WeatherIntent.SearchFieldFocused) },
                )
            }

            state.error?.let { error ->
                item {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            if (state.searchResults.isNotEmpty()) {
                items(state.searchResults, key = { "${it.latitude}_${it.longitude}_${it.name}" }) { location ->
                    LocationResultItem(
                        location = location,
                        onClick = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            onIntent(WeatherIntent.SelectLocation(location))
                        },
                    )
                }
            }

            state.forecast?.let { forecast ->
                item {
                    CurrentWeatherCard(forecast = forecast)
                }

                item {
                    Text(
                        text = "Hourly",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }

                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(forecast.hourly, key = { it.time }) { hour ->
                            HourlyItem(
                                time = hour.time,
                                temperature = hour.temperatureCelsius,
                                weatherCode = hour.weatherCode,
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Daily",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }

                items(forecast.daily, key = { it.date }) { day ->
                    DailyItem(
                        date = day.date,
                        min = day.minTemperatureCelsius,
                        max = day.maxTemperatureCelsius,
                        weatherCode = day.weatherCode,
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchSection(
    state: WeatherState,
    onSearch: () -> Unit,
    onQueryChange: (String) -> Unit,
    onSearchFieldFocused: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Weather",
            style = MaterialTheme.typography.headlineLarge,
        )
        Text(
            text = if (state.useLocation) "GPS mode" else "Search mode",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = state.query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            onSearchFieldFocused()
                        }
                    },
                placeholder = { Text("City") },
                singleLine = true,
            )
            Button(
                onClick = onSearch,
                enabled = !state.isSearching,
            ) {
                Text(if (state.isSearching) "..." else "Search")
            }
        }
    }
}

@Composable
private fun LocationResultItem(
    location: GeoLocation,
    onClick: () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
    ) {
        val subtitle = location.country?.let { "${location.name}, $it" } ?: location.name
        Text(subtitle)
    }
}

@Composable
private fun CurrentWeatherCard(
    forecast: WeatherForecast,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WeatherCodeIcon(
                code = forecast.current.weatherCode,
                modifier = Modifier.size(72.dp),
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = forecast.locationName,
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatTemperature(forecast.current.temperatureCelsius),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = weatherCodeDescription(forecast.current.weatherCode),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}

@Composable
private fun HourlyItem(
    time: String,
    temperature: Double,
    weatherCode: Int,
) {
    Card {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(formatHour(time))
            Spacer(modifier = Modifier.height(4.dp))
            WeatherCodeIcon(
                code = weatherCode,
                modifier = Modifier.size(28.dp),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(formatTemperature(temperature), fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun DailyItem(
    date: String,
    min: Double,
    max: Double,
    weatherCode: Int,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(formatDay(date), modifier = Modifier.width(48.dp))
            WeatherCodeIcon(
                code = weatherCode,
                modifier = Modifier.size(24.dp),
            )
            Text(
                weatherCodeDescription(weatherCode),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
            )
            Text("${formatTemperature(min)} / ${formatTemperature(max)}")
        }
        HorizontalDivider()
    }
}
