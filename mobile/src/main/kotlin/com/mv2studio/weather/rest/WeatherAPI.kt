package com.mv2studio.weather.rest

import com.mv2studio.weather.model.ForecastResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by matej on 28/11/2016.
 */
interface WeatherAPI {

    @GET("forecast?lat={lat}&lon={lon}")
    fun getForecast(@Path("lat") lat: String, @Path("lon") lon: String) : Observable<ForecastResponse>

}