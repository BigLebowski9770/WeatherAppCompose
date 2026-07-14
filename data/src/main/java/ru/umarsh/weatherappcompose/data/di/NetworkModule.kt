package ru.umarsh.weatherappcompose.data.di

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.umarsh.weatherappcompose.data.api.ForecastApi
import ru.umarsh.weatherappcompose.data.api.GeocodingApi
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
object NetworkModule {

    private const val GEOCODING_BASE_URL = "https://geocoding-api.open-meteo.com/"
    private const val FORECAST_BASE_URL = "https://api.open-meteo.com/"

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @Named("geocoding")
    fun provideGeocodingRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(GEOCODING_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    @Named("forecast")
    fun provideForecastRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(FORECAST_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideGeocodingApi(
        @Named("geocoding") retrofit: Retrofit,
    ): GeocodingApi = retrofit.create(GeocodingApi::class.java)

    @Provides
    @Singleton
    fun provideForecastApi(
        @Named("forecast") retrofit: Retrofit,
    ): ForecastApi = retrofit.create(ForecastApi::class.java)
}
