package com.mv2studio.weather.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created by matej on 25/11/2016.
 */

class ForecastResponse(city: City, @JsonProperty("list") private val forecastItems: List<ForecastItem>) {
    @JsonIgnore
    val cityName = city.name
}

data class City(val name: String, val coord: Coordinations)

data class Coordinations(val lat: Double, val lon: Double)

class ForecastItem(@JsonProperty("dt") val time: Long, main: BasicInfo, weather: Weather) {
    @JsonIgnore val temperature = main.temp
    @JsonIgnore val weatherState = weather.name
    @JsonIgnore val weatherDescription = weather.description
    @JsonIgnore val weatherId = weather.id
}

data class BasicInfo(val temp: Double)

data class Weather(val id: Int, @JsonProperty("main") val name: String, val description: String)
