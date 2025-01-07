package com.example.histoweather

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.histoweather.api.geocoding.Place
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

/**
 * Placeholder for the location if no location is selected
 */
val NoLocation = Place(
    id = -1,
    name = "Select",
    latitude = 0.0,
    longitude = 0.0
)

/**
 * Placeholder for the current location if the location is not available
 */
val invalidCurrentLocation = Place(
    id = -2,
    name = "Current",
    latitude = 0.0,
    longitude = 0.0
)

/**
 * Request code for the location permission
 */
val REQUEST_CODE_LOCATION_PERMISSION = 100

/**
 * Service to get the current location
 */
class LocationService {

    private val activity: MainActivity?
    private val fusedLocationClient: FusedLocationProviderClient?

    constructor(
        activity: MainActivity?,
    ) {
        this.activity = activity
        this.fusedLocationClient =
            activity?.let { LocationServices.getFusedLocationProviderClient(it) }
    }

    /**
     * Request location permission from the user
     */
    private fun requestLocationPermission() {
        // function is only called if activity is not null (check happens in requestCurrentLocation)
        if (activity == null) {
            return
        }

        activity.requestPermission(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_CODE_LOCATION_PERMISSION
        )
    }

    /**
     * Check if the app has location permission
     */
    private fun hasLocationPermission(): Boolean {
        // function is only called if activity is not null (check happens in requestCurrentLocation)
        if (activity == null) {
            return false
        }

        return (ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }

    /**
     * Try to get the current location of the user
     */
    fun requestCurrentLocation() {
        if (activity == null) {
            GlobalVariables.currentLocation.value = NoLocation
            return
        }

        if (!hasLocationPermission()) {
            requestLocationPermission()
            GlobalVariables.currentLocation.value = NoLocation
            return
        }

        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // fusedLocationClient is only null if activity is null
            fusedLocationClient?.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                ?.addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        GlobalVariables.currentLocation.value = Place(
                            0,
                            "Current",
                            location.latitude,
                            location.longitude
                        )

                        // update primary and secondary location if they are using the current location
                        if (GlobalVariables.primaryLocation.value.id == 0
                            || GlobalVariables.primaryLocation.value.id == -2
                        ) {
                            GlobalVariables.primaryLocation.value =
                                GlobalVariables.currentLocation.value
                        }
                        if (GlobalVariables.secondaryLocation.value.id == 0
                            || GlobalVariables.secondaryLocation.value.id == -2
                        ) {
                            GlobalVariables.secondaryLocation.value =
                                GlobalVariables.currentLocation.value
                        }
                    } else {
                        Toast.makeText(activity, "Location not available", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}