package com.sundaytools.app.network

/** Mirrors one row of the "Blacklist" Google Sheet, as returned by getBlacklist(). */
data class RemoteBlacklistEntry(
    val id: Any? = null,
    val tag: String = "",
    val name: String? = null,
    val status: String? = null,
    val reason: String? = null,
    val dateAdded: String? = null,
    val addedBy: String? = null
)

/** Generic shape of every response from the backend's doPost JSON API. */
data class ApiResult(
    val success: Boolean = false,
    val message: String? = null,
    val id: Any? = null,
    val username: String? = null,
    val role: String? = null,
    val blacklisted: Boolean? = null,
    val entries: List<RemoteBlacklistEntry>? = null
)
