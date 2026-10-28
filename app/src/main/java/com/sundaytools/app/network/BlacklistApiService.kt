package com.sundaytools.app.network

import com.google.gson.Gson
import com.sundaytools.app.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Talks to the Google Apps Script Web App backend defined in Code.gs, which
 * exposes a small JSON API over doPost: every request is
 * `{ "action": "...", ...params }` and every response is
 * `{ "success": true/false, ... }`.
 *
 * This is what keeps the Android app and the Google Sheets database (and the
 * web dashboard) in sync: captures made on Android are pushed here via
 * [addEntry], and the same sheet can be pulled back via [getBlacklist] to
 * show entries added from the web app or other devices.
 */
@Singleton
class BlacklistApiService @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    private val gson = Gson()

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    sealed class ApiOutcome<out T> {
        data class Success<T>(val value: T) : ApiOutcome<T>()
        data class Failure(val message: String) : ApiOutcome<Nothing>()
    }

    /** Runs [action] with [params] against the configured backend URL. */
    private suspend fun call(action: String, params: Map<String, Any?> = emptyMap()): ApiOutcome<ApiResult> =
        withContext(Dispatchers.IO) {
            val url = settingsRepository.getSettings().backendUrl
            if (url.isBlank()) {
                return@withContext ApiOutcome.Failure("Backend URL is not configured (set it in Settings).")
            }

            val bodyMap = LinkedHashMap<String, Any?>()
            bodyMap["action"] = action
            bodyMap.putAll(params)

            val json = gson.toJson(bodyMap)
            val requestBody = json.toRequestBody(jsonMediaType)
            val request = Request.Builder().url(url).post(requestBody).build()

            try {
                client.newCall(request).execute().use { response ->
                    val text = response.body?.string().orEmpty()
                    if (!response.isSuccessful || text.isBlank()) {
                        return@withContext ApiOutcome.Failure("Backend error (HTTP ${response.code})")
                    }
                    val result = runCatching { gson.fromJson(text, ApiResult::class.java) }.getOrNull()
                        ?: return@withContext ApiOutcome.Failure("Unexpected response from backend")
                    ApiOutcome.Success(result)
                }
            } catch (t: Throwable) {
                ApiOutcome.Failure(t.message ?: "Network error")
            }
        }

    /** Pushes a newly captured tag into the Blacklist sheet. Best-effort: local save always succeeds regardless. */
    suspend fun addEntry(
        tag: String,
        name: String?,
        status: String = "Blacklisted",
        reason: String = "",
        addedBy: String
    ): ApiOutcome<ApiResult> = call(
        "addEntry",
        mapOf(
            "tag" to tag,
            "name" to (name ?: "Unknown"),
            "status" to status,
            "reason" to reason,
            "addedBy" to addedBy
        )
    )

    /** Fetches every row currently in the Blacklist sheet -- used to show web/other-device entries on Android. */
    suspend fun getBlacklist(): ApiOutcome<List<RemoteBlacklistEntry>> {
        return when (val outcome = call("getBlacklist")) {
            is ApiOutcome.Success -> {
                val result = outcome.value
                if (result.success) {
                    ApiOutcome.Success(result.entries.orEmpty())
                } else {
                    ApiOutcome.Failure(result.message ?: "Failed to fetch records")
                }
            }
            is ApiOutcome.Failure -> outcome
        }
    }

    /** Lightweight check for whether [tag] is already on the shared sheet, without downloading everything. */
    suspend fun checkTag(tag: String): ApiOutcome<Boolean> {
        return when (val outcome = call("checkTag", mapOf("tag" to tag))) {
            is ApiOutcome.Success -> {
                val result = outcome.value
                if (result.success) {
                    ApiOutcome.Success(result.blacklisted ?: false)
                } else {
                    ApiOutcome.Failure(result.message ?: "Failed to check tag")
                }
            }
            is ApiOutcome.Failure -> outcome
        }
    }

    /** Updates the status/reason of an already-synced row, identified by the ID returned from [addEntry]. */
    suspend fun updateEntry(
        remoteId: String,
        tag: String,
        name: String?,
        status: String,
        reason: String
    ): ApiOutcome<ApiResult> = call(
        "updateEntry",
        mapOf(
            "id" to remoteId,
            "tag" to tag,
            "name" to (name ?: "Unknown"),
            "status" to status,
            "reason" to reason
        )
    )

    /** Verifies username/password against the same Login sheet the web app uses. */
    suspend fun login(username: String, password: String): ApiOutcome<ApiResult> =
        call("login", mapOf("username" to username, "password" to password))
}
