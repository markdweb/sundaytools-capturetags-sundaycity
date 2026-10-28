package com.sundaytools.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundaytools.app.database.entity.PlayerTagEntity
import com.sundaytools.app.network.BlacklistApiService
import com.sundaytools.app.repository.PlayerTagRepository
import com.sundaytools.app.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: PlayerTagRepository,
    private val blacklistApiService: BlacklistApiService,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val todayTags: StateFlow<List<PlayerTagEntity>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) repository.observeTodayTags() else repository.searchTodayTags(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun deleteTag(id: Long) {
        viewModelScope.launch { repository.deleteTag(id) }
    }

    suspend fun exportAllTags(): List<PlayerTagEntity> = repository.getAllTags()

    /**
     * Corrects a saved entry's status/reason (e.g. it was saved as
     * "Blacklisted" right away but should really be "Thrower"). Updates the
     * local row immediately, then -- if this entry already synced to Google
     * Sheets and cloud sync is on -- pushes the same correction to that row
     * on the sheet, so the web dashboard stays consistent too.
     */
    fun updateStatus(entry: PlayerTagEntity, status: String, reason: String) {
        viewModelScope.launch {
            repository.updateStatus(entry.id, status, reason)

            val settings = settingsRepository.getSettings()
            val remoteId = entry.remoteId
            if (remoteId != null && settings.cloudSyncEnabled && settings.backendUrl.isNotBlank()) {
                blacklistApiService.updateEntry(
                    remoteId = remoteId,
                    tag = entry.tag,
                    name = entry.name,
                    status = status,
                    reason = reason
                )
            }
        }
    }
}
