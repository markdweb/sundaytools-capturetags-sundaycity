package com.sundaytools.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundaytools.app.network.BlacklistApiService
import com.sundaytools.app.network.RemoteBlacklistEntry
import com.sundaytools.app.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CloudRecordsUiState(
    val isLoading: Boolean = false,
    val entries: List<RemoteBlacklistEntry> = emptyList(),
    val errorMessage: String? = null,
    val backendConfigured: Boolean = true,
    val lastSyncedAt: Long? = null
)

/**
 * Backs the "Cloud Records" screen: pulls the same Blacklist Google Sheet the
 * web app reads/writes, so entries added from the web app (or any other
 * device) show up here without the user re-adding them on Android. The
 * screen polls this repeatedly while visible to approximate real-time sync,
 * since Apps Script Web Apps can't push updates to clients.
 */
@HiltViewModel
class CloudRecordsViewModel @Inject constructor(
    private val api: BlacklistApiService,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CloudRecordsUiState())
    val uiState: StateFlow<CloudRecordsUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    /** Filters the currently loaded entries by tag or name -- purely client-side, no extra network call. */
    fun filteredEntries(): List<RemoteBlacklistEntry> {
        val query = _searchQuery.value.trim().lowercase()
        val entries = _uiState.value.entries
        if (query.isBlank()) return entries
        return entries.filter { entry ->
            entry.tag.lowercase().contains(query) || entry.name.orEmpty().lowercase().contains(query)
        }
    }

    fun refresh() {
        val backendUrl = settingsRepository.getSettings().backendUrl
        if (backendUrl.isBlank()) {
            _uiState.value = _uiState.value.copy(backendConfigured = false, isLoading = false)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, backendConfigured = true)
            when (val outcome = api.getBlacklist()) {
                is BlacklistApiService.ApiOutcome.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        entries = outcome.value,
                        errorMessage = null,
                        lastSyncedAt = System.currentTimeMillis()
                    )
                }
                is BlacklistApiService.ApiOutcome.Failure -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = outcome.message
                    )
                }
            }
        }
    }
}
