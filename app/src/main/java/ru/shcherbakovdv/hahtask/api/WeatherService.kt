package ru.shcherbakovdv.hahtask.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import ru.shcherbakovdv.hahtask.Weather

interface WeatherService {

    @GET("data/2.5/weather?q=Saransk,ru&appid=15f9ec09070de74ef6ae363c2d99f3de")
    suspend fun getWeather(): Weather

    companion object {
        private const val BASE_URL = "https://samples.openweathermap.org/"

        fun create(): WeatherService = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherService::class.java)
    }

}