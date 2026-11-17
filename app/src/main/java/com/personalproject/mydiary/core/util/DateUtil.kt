package com.personalproject.mydiary.core.util

import java.lang.Math.random
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class DateUtil {
    companion object {
        private val zoneID = ZoneId.systemDefault()

        /**
         * Returns string representation the day of the week.
         * @property instant The given timestamp.
         */
        fun dayOfWeek(instant: Instant?, style: TextStyle = TextStyle.SHORT) : String {
            try {
                val dateTime = instant?.atZone(zoneID)
                val dayOfWeek = dateTime?.dayOfWeek
                return dayOfWeek?.getDisplayName(style, Locale.ENGLISH).plus(".") ?: "null"
            } catch (e: Exception) {
                return "[ERROR] Failed to get day of week in DateUtil: $e\n"
            }
        }

        /**
         * Return the string representation of the time(24-hour format) of day in
         * a given timestamp
         * @property instant The given timestamp.
         */
        fun getTime(instant: Instant?) : String {
            try {
                val dateTime = instant?.atZone(zoneID)
                val formatter = DateTimeFormatter.ofPattern("H:mm", Locale.ENGLISH)
                return dateTime?.format(formatter) ?: "null"
            } catch (e: Exception) {
                return "[ERROR] Failed to get time in DateUtil: $e\n"
            }
        }

        /**
         * Return the numeric string representation of the day of month in
         * a given timestamp
         * @property instant The given timestamp.
         */
        fun getDayOfMonth(instant: Instant?) : String {
            try {
                val dateTime = instant?.atZone(zoneID)
                return dateTime?.dayOfMonth.toString()
            } catch (e: Exception) {
                return "[ERROR] Failed to get day of month in DateUtil: $e\n"
            }
        }

        /**
         * Return the numeric string representation of the month value in
         * a given timestamp
         * @property instant The given timestamp.
         */
        fun getMonthNumeric(instant: Instant) : String {
            try {
                val dateTime = instant.atZone(zoneID)
                return dateTime.monthValue.toString()
            } catch (e: Exception) {
                return "[ERROR] Failed to get month value in DateUtil: $e\n"
            }
        }

        /**
         * Return the numeric string representation of the month in
         * a given timestamp
         * @property instant The given timestamp.
         */
        fun getMonthString(instant: Instant?) : String {
            try {
                val dateTime = instant?.atZone(zoneID)
                return dateTime?.month?.getDisplayName(TextStyle.FULL, Locale.ENGLISH) ?: "null"
            } catch (e: Exception) {
                return "[ERROR] Failed to get month in DateUtil: $e\n"
            }
        }

        fun formatDateMonth(instant: Instant?): String {
            val formatter = DateTimeFormatter.ofPattern("d MMM", Locale.getDefault())
                .withZone(ZoneId.systemDefault())

            return formatter.format(instant) // e.g., "17 Nov"
        }

        fun daysAgo(instant: Instant, day: Long) : Instant {
            return try {
                instant.minusSeconds(((day - random()) * 86400).toLong())
            } catch (e: Exception) {
                instant
            }
        }
    }
}