# Architecture notes

Pet-проект погоды. Стек: Compose, MVI, Dagger 2, Flow, Retrofit, Room.

## Модули

```
:app              — Activity, NavHost, AppComponent
:core:common      — dispatchers, утилиты (kotlin jvm)
:core:navigation  — routes
:core:ui          — theme
:domain           — models, интерфейсы репозиториев
:data             — retrofit, room, repo impl, @Module
:feature:splash   — splash + location permission
:feature:weather  — экран погоды
```

Зависимости:
- `app` тянет feature + data + core
- feature между собой не завязаны
- `data -> domain -> core:common`

Отдельный `:feature:x:api` не делал — фич две, routes в `core:navigation`, 
entry point — `NavGraphBuilder.splashScreen()` / `weatherScreen()` в feature-модуле.

`core:navigation` вынес отдельно, чтобы feature не импортили друг друга и не тащили лишний compose.

## User flow

1. Splash — анимация (TODO), init, запрос location
2. Navigate на Weather, `popUpTo(Splash) { inclusive = true }`
3. Permission есть → `WeatherRoute(useLocation = true)` → GPS → forecast API
4. Permission нет → поиск города → geocoding → forecast

Runtime permission только `ACCESS_FINE_LOCATION`. `INTERNET` — manifest.

## Open-Meteo

Без ключа.

- Geocoding: `https://geocoding-api.open-meteo.com/v1/search?name=Almaty`
- Forecast: `https://api.open-meteo.com/v1/forecast?latitude=...&longitude=...&current=...&hourly=...&daily=...`

В data планирую 2 retrofit (разные base url), один okhttp.

## Navigation

NavHost только в app:

```kotlin
NavHost(navController, startDestination = SplashRoute) {
    splashScreen { useLocation -> navController.navigate(WeatherRoute(useLocation)) { ... } }
    weatherScreen()
}
```

Routes (`core/navigation/Routes.kt`):

```kotlin
@Serializable data object SplashRoute
@Serializable data class WeatherRoute(val useLocation: Boolean = false)
```

## Dagger

`AppComponent` — singleton, поднимается в `WeatherApplication.onCreate()`.

Сейчас там network/db/repo stubs + `DispatcherProvider`. ViewModel-зависимости фич пока не тащу в root component.

Subcomponents создаются не автоматически — только когда явно вызвать factory. 
План: позже `SplashComponent` / `WeatherComponent` для location helper и т.п.

AGP 9: kapt не работает с built-in kotlin, использую KSP. 
В `gradle.properties` есть `android.disallowKotlinSourceSets=false` — workaround под ksp.

## MVI (шпаргалка, пока разбираюсь)

- **State** — что рисуем, `StateFlow`
- **Intent** — клики/события, `onIntent(...)`
- **Effect** — snackbar, navigation one-shot, `SharedFlow`

ViewModel держит state/effect, UI collect через `collectAsStateWithLifecycle` + `LaunchedEffect` для effects.

## Слои

```
feature (UI + ViewModel)
  -> domain (interfaces, models)
    -> data (retrofit, room, impl)
```

Domain без android/retrofit/room.

## Дальше

- [x] architecture — модули, nav skeleton, dagger skeleton
- [ ] data-layer — retrofit, room, нормальный repository
- [ ] feature-splash — анимация, mvi
- [ ] feature-weather — ui + api
- [ ] polish — ошибки, кэш, тесты

Версии — `gradle/libs.versions.toml`.
