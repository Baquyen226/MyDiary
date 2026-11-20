package com.personalproject.mydiary.core.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.core.os.CancellationSignal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.time.Duration.Companion.milliseconds

/**
 * Reusable utility for checking location permissions, retrieving device location,
 * and geocoding coordinates into readable location names.
 */
object LocationUtil {

    const val UNKNOWN_LOCATION = "UNKNOWN"

    /**
     * Standard location permissions required to obtain fine and/or coarse device location.
     */
    val LOCATION_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    /**
     * Checks if either fine or coarse location permission is granted in a permission result map.
     */
    fun isAnyPermissionGranted(permissions: Map<String, Boolean>): Boolean {
        return permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    /**
     * Checks if either fine or coarse location permission is granted.
     */
    fun hasLocationPermission(context: Context): Boolean {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineGranted || coarseGranted
    }

    /**
     * Checks if device location services (GPS or Network) are enabled.
     */
    fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return false
        return LocationManagerCompat.isLocationEnabled(locationManager)
    }

    /**
     * Retrieves the most accurate and recent last known location from any available provider.
     */
    @SuppressLint("MissingPermission")
    fun getLastKnownLocation(context: Context): Location? {
        if (!hasLocationPermission(context)) return null

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return null

        val providers = try {
            locationManager.getProviders(true)
        } catch (e: SecurityException) {
            emptyList()
        }

        var bestLocation: Location? = null
        for (provider in providers) {
            try {
                val loc = locationManager.getLastKnownLocation(provider) ?: continue
                if (bestLocation == null || loc.accuracy < bestLocation.accuracy || loc.time > bestLocation.time) {
                    bestLocation = loc
                }
            } catch (_: SecurityException) {
                // Ignore security exceptions if permissions change dynamically
            } catch (_: Exception) {
                // Ignore provider failures
            }
        }
        return bestLocation
    }

    /**
     * Attempts to retrieve current location, falling back to [getLastKnownLocation] if fresh
     * or if retrieval times out.
     */
    @SuppressLint("MissingPermission")
    @Suppress("DEPRECATION")
    suspend fun getCurrentLocation(context: Context, timeoutMs: Long = 2000L): Location? {
        if (!hasLocationPermission(context)) return null

        val lastKnown = getLastKnownLocation(context)
        val isFresh = lastKnown != null && (System.currentTimeMillis() - lastKnown.time < 1000 * 60 * 10)
        if (isFresh) {
            return lastKnown
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return lastKnown

        return withTimeoutOrNull(timeoutMs.milliseconds) {
            suspendCancellableCoroutine { continuation ->
                val cancellationSignal = CancellationSignal()
                continuation.invokeOnCancellation {
                    cancellationSignal.cancel()
                }

                val executor = ContextCompat.getMainExecutor(context)
                val provider = when {
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
                    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
                    else -> LocationManager.PASSIVE_PROVIDER
                }

                try {
                    LocationManagerCompat.getCurrentLocation(
                        locationManager,
                        provider,
                        cancellationSignal,
                        executor
                    ) { location ->
                        if (continuation.isActive) {
                            continuation.resume(location ?: lastKnown)
                        }
                    }
                } catch (_: SecurityException) {
                    if (continuation.isActive) continuation.resume(lastKnown)
                } catch (_: Exception) {
                    if (continuation.isActive) continuation.resume(lastKnown)
                }
            }
        } ?: lastKnown
    }

    /**
     * Removes postal codes / zip codes (e.g. "160-0023", "75001", "94105-1234") and cleans
     * redundant spaces and commas from an address string.
     */
    fun cleanPostalCode(text: String, postalCode: String? = null): String {
        var cleaned = text
        if (!postalCode.isNullOrBlank()) {
            cleaned = cleaned.replace(postalCode, "")
        }
        // Match standard postal code patterns (e.g. 160-0023, 94105, 94105-1234)
        cleaned = cleaned.replace(Regex("""\b\d{3}-\d{4}\b|\b\d{5}(-\d{4})?\b"""), "")
        // Clean up redundant commas, spaces, and trimming
        cleaned = cleaned
            .replace(Regex("""\s*,\s*,"""), ",")
            .replace(Regex("""\s+"""), " ")
            .replace(Regex("""\s*,\s*"""), ", ")
            .trim(' ', ',')
        return cleaned
    }

    /**
     * Formats an [Address] into a readable location string without zip codes.
     * Aligns with the mock diary format (e.g., "Shinjuku City, Tokyo, Japan" or "Paris, France").
     */
    fun formatAddress(address: Address): String {
        val postalCode = address.postalCode

        val cityOrDistrict = address.locality ?: address.subLocality ?: address.subAdminArea
        val stateOrPrefecture = address.adminArea
        val country = address.countryName

        val parts = linkedSetOf<String>()

        if (!cityOrDistrict.isNullOrBlank()) {
            val cleanCity = cleanPostalCode(cityOrDistrict, postalCode)
            if (cleanCity.isNotBlank()) parts.add(cleanCity)
        }

        if (!stateOrPrefecture.isNullOrBlank() && !parts.any { it.equals(stateOrPrefecture, ignoreCase = true) }) {
            val cleanState = cleanPostalCode(stateOrPrefecture, postalCode)
            if (cleanState.isNotBlank()) parts.add(cleanState)
        }

        if (!country.isNullOrBlank() && !parts.any { it.equals(country, ignoreCase = true) }) {
            val cleanCountry = cleanPostalCode(country, postalCode)
            if (cleanCountry.isNotBlank()) parts.add(cleanCountry)
        }

        val result = parts.joinToString(", ")
        if (result.isNotBlank()) {
            return result
        }

        // Fallback to full address line with zipcode stripped
        val addressLine = address.getAddressLine(0) ?: ""
        return cleanPostalCode(addressLine, postalCode)
    }

    /**
     * Reverse geocodes a [Location] into a human-readable city/region/country string without zip codes.
     * Executes on [Dispatchers.IO] to avoid NetworkOnMainThreadException.
     */
    suspend fun getAddressFromLocation(context: Context, location: Location): String? = withContext(Dispatchers.IO) {
        try {
            if (!Geocoder.isPresent()) return@withContext null
            val geocoder = Geocoder(context, Locale.getDefault())
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val formatted = formatAddress(addresses[0])
                formatted.ifBlank { null }
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Automatically resolves the current location name.
     * - Returns [UNKNOWN_LOCATION] if location permission is not granted.
     * - Returns a human-readable address if available (e.g. "Paris, France").
     * - Returns formatted coordinates if address lookup is unavailable.
     * - Returns [UNKNOWN_LOCATION] if location cannot be retrieved.
     */
    suspend fun resolveLocation(context: Context): String {
        if (!hasLocationPermission(context)) {
            return UNKNOWN_LOCATION
        }

        return try {
            val location = getCurrentLocation(context) ?: return UNKNOWN_LOCATION
            val addressName = getAddressFromLocation(context, location)
            if (!addressName.isNullOrBlank()) {
                addressName
            } else {
                String.format(Locale.US, "%.4f, %.4f", location.latitude, location.longitude)
            }
        } catch (_: Exception) {
            UNKNOWN_LOCATION
        }
    }

    /**
     * Synchronous variant that resolves location name using [getLastKnownLocation].
     * Returns [UNKNOWN_LOCATION] if permission is missing or location is unavailable.
     */
    fun resolveLocationSync(context: Context): String {
        if (!hasLocationPermission(context)) {
            return UNKNOWN_LOCATION
        }
        val location = getLastKnownLocation(context) ?: return UNKNOWN_LOCATION
        return String.format(Locale.US, "%.4f, %.4f", location.latitude, location.longitude)
    }
}
