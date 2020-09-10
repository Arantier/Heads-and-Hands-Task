package ru.shcherbakovdv.hahtask

// Of course in real implementation it would be user's data. But because now I have to return
// weather data after "authentification" this class returns weather data
data class Weather constructor(
    val temperature: Float,
    val pressure: Float,
    val humidity: Float,
    val windSpeed: Float,
    val windDirection: Int,
    val clouds: Int
)