package com.sundaytools.app.repository

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Holds user-configurable app settings. Backed by SharedPreferences for simplicity
 * and speed of access from the overlay service (which needs synchronous reads).
 *
 * [hasCaptureRegion]/[regionLeft]/[regionTop]/[regionRight]/[regionBottom] define
 * an optional rectangular area (as fractions 0..1 of the screen) that OCR should
 * be restricted to. When [hasCaptureRegion] is false, OCR scans the whole screen.
 * Restricting to a region avoids the OCR picking up unrelated on-screen text
 * (timers, currency, chat) that happens to look like a tag.
 *
 * [blacklistWarningTemplate] is the message copied when the user taps
 * "Copy Warning" on a tag that's already on record. Supports the placeholders
 * {name} and {tag}, which get substituted with the scanned player's info.
 *
 * [backendUrl] is the deployed Google Apps Script Web App "exec" URL used to
 * sync captured tags into the shared Google Sheets database. [addedByName] is
 * the label recorded in the sheet's "Added By" column for entries created
 * from this device (matches the web app's "Added By" concept).
 */
data class AppSettings(
    val autoCopyEnabled: Boolean = true,
    val vibrateOnScan: Boolean = true,
    val soundOnScan: Boolean = false,
    val darkMode: Boolean = false,
    val archiveHour: Int = 23,
    val archiveMinute: Int = 0,
    val hasCaptureRegion: Boolean = false,
    val regionLeft: Float = 0f,
    val regionTop: Float = 0f,
    val regionRight: Float = 1f,
    val regionBottom: Float = 1f,
    val blacklistWarningTemplate: String = DEFAULT_BLACKLIST_TEMPLATE,
    val backendUrl: String = "",
    val addedByName: String = "android",
    val cloudSyncEnabled: Boolean = true
)

const val DEFAULT_BLACKLIST_TEMPLATE =
    "SCAMMER: {name} ({tag}) IS BLACKLISTED FROM ALL FUTURE PRIVATE PARTIES AND IS NO LONGER ALLOWED TO PARTICIPATE."

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("sundaytools_settings", Context.MODE_PRIVATE)

    fun getSettings(): AppSettings = AppSettings(
        autoCopyEnabled = prefs.getBoolean(KEY_AUTO_COPY, true),
        vibrateOnScan = prefs.getBoolean(KEY_VIBRATE, true),
        soundOnScan = prefs.getBoolean(KEY_SOUND, false),
        darkMode = prefs.getBoolean(KEY_DARK_MODE, false),
        archiveHour = prefs.getInt(KEY_ARCHIVE_HOUR, 23),
        archiveMinute = prefs.getInt(KEY_ARCHIVE_MINUTE, 0),
        hasCaptureRegion = prefs.getBoolean(KEY_HAS_REGION, false),
        regionLeft = prefs.getFloat(KEY_REGION_LEFT, 0f),
        regionTop = prefs.getFloat(KEY_REGION_TOP, 0f),
        regionRight = prefs.getFloat(KEY_REGION_RIGHT, 1f),
        regionBottom = prefs.getFloat(KEY_REGION_BOTTOM, 1f),
        blacklistWarningTemplate = prefs.getString(KEY_BLACKLIST_TEMPLATE, null) ?: DEFAULT_BLACKLIST_TEMPLATE,
        backendUrl = prefs.getString(KEY_BACKEND_URL, null) ?: "",
        addedByName = prefs.getString(KEY_ADDED_BY, null) ?: "android",
        cloudSyncEnabled = prefs.getBoolean(KEY_CLOUD_SYNC_ENABLED, true)
    )

    fun observeSettings(): Flow<AppSettings> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            trySend(getSettings())
        }
        trySend(getSettings())
        prefs.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    fun setAutoCopy(enabled: Boolean) = prefs.edit().putBoolean(KEY_AUTO_COPY, enabled).apply()
    fun setVibrateOnScan(enabled: Boolean) = prefs.edit().putBoolean(KEY_VIBRATE, enabled).apply()
    fun setSoundOnScan(enabled: Boolean) = prefs.edit().putBoolean(KEY_SOUND, enabled).apply()
    fun setDarkMode(enabled: Boolean) = prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply()

    fun setArchiveTime(hour: Int, minute: Int) = prefs.edit()
        .putInt(KEY_ARCHIVE_HOUR, hour)
        .putInt(KEY_ARCHIVE_MINUTE, minute)
        .apply()

    /** Stores the OCR capture area as fractions (0..1) of the full screen. */
    fun setCaptureRegion(left: Float, top: Float, right: Float, bottom: Float) = prefs.edit()
        .putBoolean(KEY_HAS_REGION, true)
        .putFloat(KEY_REGION_LEFT, left)
        .putFloat(KEY_REGION_TOP, top)
        .putFloat(KEY_REGION_RIGHT, right)
        .putFloat(KEY_REGION_BOTTOM, bottom)
        .apply()

    /** Reverts OCR to scanning the whole screen. */
    fun clearCaptureRegion() = prefs.edit().putBoolean(KEY_HAS_REGION, false).apply()

    fun setBlacklistWarningTemplate(template: String) =
        prefs.edit().putString(KEY_BLACKLIST_TEMPLATE, template).apply()

    fun setBackendUrl(url: String) = prefs.edit().putString(KEY_BACKEND_URL, url.trim()).apply()
    fun setAddedByName(name: String) = prefs.edit().putString(KEY_ADDED_BY, name.trim()).apply()
    fun setCloudSyncEnabled(enabled: Boolean) = prefs.edit().putBoolean(KEY_CLOUD_SYNC_ENABLED, enabled).apply()

    companion object {
        private const val KEY_AUTO_COPY = "auto_copy"
        private const val KEY_VIBRATE = "vibrate_on_scan"
        private const val KEY_SOUND = "sound_on_scan"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_ARCHIVE_HOUR = "archive_hour"
        private const val KEY_ARCHIVE_MINUTE = "archive_minute"
        private const val KEY_HAS_REGION = "has_capture_region"
        private const val KEY_REGION_LEFT = "region_left"
        private const val KEY_REGION_TOP = "region_top"
        private const val KEY_REGION_RIGHT = "region_right"
        private const val KEY_REGION_BOTTOM = "region_bottom"
        private const val KEY_BLACKLIST_TEMPLATE = "blacklist_warning_template"
        private const val KEY_BACKEND_URL = "backend_url"
        private const val KEY_ADDED_BY = "added_by_name"
        private const val KEY_CLOUD_SYNC_ENABLED = "cloud_sync_enabled"
    }
}
