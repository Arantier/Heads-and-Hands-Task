package ru.shcherbakovdv.hahtask.data

import ru.shcherbakovdv.hahtask.api.WeatherService

class LoginRepository private constructor() {

    suspend fun authenticateUser(email: String, password: String): Weather =
        WeatherService.create().getWeather()

    companion object {
        @Volatile
        private var instance: LoginRepository? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: LoginRepository().also { instance = it }
        }
    }
}