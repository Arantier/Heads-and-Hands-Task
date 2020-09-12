package ru.shcherbakovdv.hahtask

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

// Of course in real implementation it would be user's data. But because now I have to return
// weather data after "authentification" this class returns weather data
data class Weather constructor(
    val temperature: Float,
    val pressure: Float,
    val humidity: Float,
    val windSpeed: Float,
    val windDirection: Int,
    val clouds: Int
) {
    companion object {
        val adapter = GsonBuilder()
            .registerTypeAdapter(Weather::class.java, object : JsonDeserializer<Weather> {
                override fun deserialize(
                    json: JsonElement?,
                    typeOfT: Type?,
                    context: JsonDeserializationContext?
                ): Weather =
                    json?.asJsonObject?.let {
                        Weather(
                            it["main"].asJsonObject["temp"].asFloat,
                            it["main"].asJsonObject["pressure"].asFloat,
                            it["main"].asJsonObject["humidity"].asFloat,
                            it["wind"].asJsonObject["speed"].asFloat,
                            it["wind"].asJsonObject["deg"].asInt,
                            it["clouds"].asJsonObject["all"].asInt
                        )
                    } ?: throw NullPointerException()

            })
            .create()
    }
}