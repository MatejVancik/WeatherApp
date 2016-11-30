package com.mv2studio.weather.location

import android.app.Activity
import android.app.Application
import android.content.IntentSender.SendIntentException
import android.location.Location
import android.os.Bundle
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationServices.FusedLocationApi
import com.google.android.gms.location.LocationSettingsStatusCodes.RESOLUTION_REQUIRED
import com.google.android.gms.location.LocationSettingsStatusCodes.SUCCESS
import com.mv2studio.weather.App
import java.util.*


/**
 * Created by matej on 28/11/2016.
 */
object LocationService : GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private val REQUEST_CHECK_SETTINGS = 56
    val googleApiClient: GoogleApiClient
    val locationRequest = LocationRequest()
    var isTracking = false
    var lastLocation: Location? = null
    val locationListeners = HashSet<LocationUpdateCallback>()

    init {
        googleApiClient = GoogleApiClient.Builder(App.appContext!!)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build()

        locationRequest.interval = 1000
        locationRequest.fastestInterval = 100
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        googleApiClient.connect()
    }

    override fun onConnectionFailed(p0: ConnectionResult) { /*Well, now what?*/ }

    override fun onConnected(connectionHint: Bundle?) {
        lastLocation = FusedLocationApi.getLastLocation(googleApiClient)

    }

    override fun onConnectionSuspended(p0: Int) { /*Nothing to do, no updates will be provided*/ }

    override fun onLocationChanged(location: Location?) {
        lastLocation = location
        locationListeners.forEach { it.onLocationChanged(lastLocation) }
    }

    fun registerForLocationUpdates(locationUpdateCallback: LocationUpdateCallback) {
        locationListeners.add(locationUpdateCallback)
    }

    fun unregisterForLocationUpdates(locationUpdateCallback: LocationUpdateCallback) {
        locationListeners.remove(locationUpdateCallback)
    }

    fun stopLocationTracking() {
        FusedLocationApi.removeLocationUpdates(googleApiClient, this)
    }

    /**
     * Call this at point when you want to start tracking. Pass activity as this can trigger
     * new dialog which asks user to enable location.
     * This can also ask for permissions on android 6 and higher.
     *
     * Do not forget to implement @see android.app.Activity#onActivityResult
     */
    fun startLocationTracking(activity: Activity) {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())

        result.setResultCallback { result ->
            val status = result.status
            val locationSettingsStates = result.locationSettingsStates
            // All location settings are satisfied. The client can
            // initialize location requests here.
            when (status.statusCode) {
                SUCCESS -> FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this@LocationService)
                RESOLUTION_REQUIRED -> {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        status.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS)
                    } catch (e: SendIntentException) { /* Ignore the error. */ }

                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    // Location settings are not satisfied. However, we have no way
                    // to fix the settings so we won't show the dialog.
                }
            }
        }
    }

    val activityCallbacks = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityPaused(activity: Activity?) {
            if (activity !is LocationUpdateCallback) return
        }

        override fun onActivityStarted(activity: Activity?) {
            if (activity !is LocationUpdateCallback) return
            // If the client is already connected or connecting, this method does nothing.
            googleApiClient.connect()
            locationListeners.add(activity)
        }

        override fun onActivityDestroyed(activity: Activity?) {
            if (activity !is LocationUpdateCallback) return
        }

        override fun onActivitySaveInstanceState(activity: Activity?, p1: Bundle?) {
            if (activity !is LocationUpdateCallback) return
        }

        override fun onActivityStopped(activity: Activity?) {
            if (activity !is LocationUpdateCallback) return
            locationListeners.remove(activity)
        }

        override fun onActivityCreated(activity: Activity?, p1: Bundle?) {
            if (activity !is LocationUpdateCallback) return
        }

        override fun onActivityResumed(activity: Activity?) {
            if (activity !is LocationUpdateCallback) return
            activity.onLocationChanged(lastLocation)
        }
    }

    init {
        App.appContext!!.registerActivityLifecycleCallbacks(activityCallbacks)
    }

}