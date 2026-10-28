package com.sundaytools.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundaytools.app.repository.AppSettings
import com.sundaytools.app.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val settings: StateFlow<AppSettings> = settingsRepository.observeSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), settingsRepository.getSettings())

    fun setAutoCopy(enabled: Boolean) = settingsRepository.setAutoCopy(enabled)
    fun setVibrateOnScan(enabled: Boolean) = settingsRepository.setVibrateOnScan(enabled)
    fun setSoundOnScan(enabled: Boolean) = settingsRepository.setSoundOnScan(enabled)
    fun setDarkMode(enabled: Boolean) = settingsRepository.setDarkMode(enabled)
    fun setArchiveTime(hour: Int, minute: Int) = settingsRepository.setArchiveTime(hour, minute)
    fun clearCaptureRegion() = settingsRepository.clearCaptureRegion()
    fun setBlacklistWarningTemplate(template: String) = settingsRepository.setBlacklistWarningTemplate(template)
    fun setBackendUrl(url: String) = settingsRepository.setBackendUrl(url)
    fun setAddedByName(name: String) = settingsRepository.setAddedByName(name)
    fun setCloudSyncEnabled(enabled: Boolean) = settingsRepository.setCloudSyncEnabled(enabled)
}
