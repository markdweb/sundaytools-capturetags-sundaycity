package com.sundaytools.app.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Centralizes all date formatting so the "day" boundary used for grouping tags
 * ("Today" vs a History date) is consistent across the DAO, workers, and UI.
 */
object DateUtils {

    private fun dayKeyFormatter(): SimpleDateFormat =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            timeZone = TimeZone.getDefault()
        }

    private fun timeFormatter(): SimpleDateFormat =
        SimpleDateFormat("h:mm a", Locale.getDefault()).apply {
            timeZone = TimeZone.getDefault()
        }

    private fun displayDateFormatter(): SimpleDateFormat =
        SimpleDateFormat("MMMM d", Locale.getDefault()).apply {
            timeZone = TimeZone.getDefault()
        }

    fun todayKey(): String = dayKeyFormatter().format(Date())

    fun dayKeyFor(epochMillis: Long): String = dayKeyFormatter().format(Date(epochMillis))

    fun formatTime(epochMillis: Long): String = timeFormatter().format(Date(epochMillis))

    /** Converts a "yyyy-MM-dd" dayKey into a human friendly label like "July 12". */
    fun formatDayKeyForDisplay(dayKey: String): String {
        val parsed = dayKeyFormatter().parse(dayKey) ?: return dayKey
        return displayDateFormatter().format(parsed)
    }
}
