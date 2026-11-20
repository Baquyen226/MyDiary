package com.personalproject.mydiary.core.util

import android.Manifest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LocationUtilTest {

    @Test
    fun testUnknownLocationConstant() {
        assertEquals("UNKNOWN", LocationUtil.UNKNOWN_LOCATION)
    }

    @Test
    fun testLocationPermissionsContainsBothFineAndCoarse() {
        assertTrue(LocationUtil.LOCATION_PERMISSIONS.contains(Manifest.permission.ACCESS_FINE_LOCATION))
        assertTrue(LocationUtil.LOCATION_PERMISSIONS.contains(Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    @Test
    fun testIsAnyPermissionGranted() {
        // Both granted
        assertTrue(
            LocationUtil.isAnyPermissionGranted(
                mapOf(
                    Manifest.permission.ACCESS_FINE_LOCATION to true,
                    Manifest.permission.ACCESS_COARSE_LOCATION to true
                )
            )
        )

        // Only fine granted
        assertTrue(
            LocationUtil.isAnyPermissionGranted(
                mapOf(
                    Manifest.permission.ACCESS_FINE_LOCATION to true,
                    Manifest.permission.ACCESS_COARSE_LOCATION to false
                )
            )
        )

        // Only coarse granted
        assertTrue(
            LocationUtil.isAnyPermissionGranted(
                mapOf(
                    Manifest.permission.ACCESS_FINE_LOCATION to false,
                    Manifest.permission.ACCESS_COARSE_LOCATION to true
                )
            )
        )

        // Both denied
        assertFalse(
            LocationUtil.isAnyPermissionGranted(
                mapOf(
                    Manifest.permission.ACCESS_FINE_LOCATION to false,
                    Manifest.permission.ACCESS_COARSE_LOCATION to false
                )
            )
        )

        // Empty map
        assertFalse(
            LocationUtil.isAnyPermissionGranted(emptyMap())
        )
    }

    @Test
    fun testCleanPostalCodeRemovesZipCodes() {
        val testInput = "Shinjuku City, Tokyo 160-0023, Japan"
        val result = LocationUtil.cleanPostalCode(testInput, "160-0023")
        assertEquals("Shinjuku City, Tokyo, Japan", result)
    }

    @Test
    fun testCleanPostalCodeWithRegexFallback() {
        val testInput = "Shinjuku City, Tokyo 160-0023, Japan"
        val result = LocationUtil.cleanPostalCode(testInput, null)
        assertEquals("Shinjuku City, Tokyo, Japan", result)
    }

    @Test
    fun testCleanPostalCodeUSZip() {
        val testInput = "San Francisco, CA 94105, USA"
        val result = LocationUtil.cleanPostalCode(testInput, "94105")
        assertEquals("San Francisco, CA, USA", result)
    }

    @Test
    fun testCleanPostalCodeLeavesCleanAddressUntouched() {
        val testInput = "Paris, France"
        val result = LocationUtil.cleanPostalCode(testInput, null)
        assertEquals("Paris, France", result)
    }
}
