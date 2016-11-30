package com.mv2studio.weather.rest

import com.mv2studio.weather.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit

/**
 * Created by matej on 28/11/2016.
 */
object Rest {
    private val retrofit: Retrofit

    init {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor {
            val originalRequest = it.request()
            val originalUrl = originalRequest.url()

            val url = originalUrl.newBuilder()
                    .addQueryParameter("appid", BuildConfig.WEATHER_API_KEY).build()

            it.proceed(originalRequest.newBuilder().url(url).build())
        }

        retrofit = Retrofit.Builder()
                .client(httpClient.build())
                .baseUrl("http://api.openweathermap.org/data/2.5/")
                .addCallAdapterFactory(RxCallAdapter())
                .build()
    }

    val weatherAPI by lazy { retrofit.create(WeatherAPI::class.java) }

}