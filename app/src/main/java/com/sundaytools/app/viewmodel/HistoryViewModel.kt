package com.sundaytools.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundaytools.app.repository.PlayerTagRepository
import com.sundaytools.app.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HistoryDayUi(
    val dayKey: String,
    val displayLabel: String,
    val tagCount: Int
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: PlayerTagRepository
) : ViewModel() {

    private val counts = MutableStateFlow<Map<String, Int>>(emptyMap())

    val historyDays: StateFlow<List<HistoryDayUi>> = repository.observeHistoryDayKeys()
        .combine(counts) { dayKeys, countMap ->
            // Excludes today from "History" per spec (today lives on the Home screen).
            val todayKey = DateUtils.todayKey()
            dayKeys.filter { it != todayKey }.map { dayKey ->
                HistoryDayUi(
                    dayKey = dayKey,
                    displayLabel = DateUtils.formatDayKeyForDisplay(dayKey),
                    tagCount = countMap[dayKey] ?: 0
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        refreshCounts()
    }

    private fun refreshCounts() {
        viewModelScope.launch {
            val allTags = repository.getAllTags()
            counts.value = allTags.groupingBy { it.dayKey }.eachCount()
        }
    }

    companion object
}
