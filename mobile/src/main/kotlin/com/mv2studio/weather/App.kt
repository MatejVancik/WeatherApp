package com.mv2studio.weather

import android.app.Application

/**
 * Created by matej on 25/11/2016.
 */
class App : Application() {

    companion object {
        var appContext: App? = null
            private set
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
    }

}