package com.mv2studio.weather.location

import android.location.Location

/**
 * Created by matej on 29/11/2016.
 */
interface LocationUpdateCallback {

    /**
     * Called each time location is updated and when activity is resumed (if activity implements this interface)
     */
    fun onLocationChanged(location: Location?)

}