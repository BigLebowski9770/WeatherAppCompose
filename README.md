# WeatherAppCompose

Pet-проект Android-приложения погоды. Compose, multi-module, MVI.

Погода по GPS или поиску города. Данные — [Open-Meteo](https://open-meteo.com/), API-ключ не нужен.

## Что умеет

- Splash с анимацией и запросом геолокации
- Автозагрузка погоды по GPS с reverse geocoding (название города вместо «Current location»)
- Поиск города, если permission не дали
- Текущая погода, почасовой и недельный прогноз
- Pull-to-refresh, иконки по WMO-коду

## Стек

Kotlin · Jetpack Compose · Material 3 · MVI · Clean Architecture · Dagger 2 (KSP) · Retrofit · Room · Coroutines / Flow · Navigation Compose

minSdk 26 · targetSdk 37 · AGP 9

## Модули

| Модуль | Зачем |
|--------|-------|
| `app` | Activity, NavHost, DI |
| `core:common` | dispatchers, утилиты |
| `core:navigation` | type-safe routes |
| `core:ui` | тема |
| `domain` | модели, интерфейсы |
| `data` | сеть, Room, GPS |
| `feature:splash` | splash + permission |
| `feature:weather` | экран погоды, ViewModel |

Подробнее — [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

## Запуск

```bash
git clone https://github.com/BigLebowski9770/WeatherAppCompose.git
```

Открыть в Android Studio, sync Gradle, Run на эмуляторе или устройстве.  
Для GPS нужен эмулятор с заданной локацией или реальное устройство.

## API

- Geocoding: `geocoding-api.open-meteo.com`
- Forecast: `api.open-meteo.com`

Спасибо [Open-Meteo](https://open-meteo.com/) за бесплатный API.
