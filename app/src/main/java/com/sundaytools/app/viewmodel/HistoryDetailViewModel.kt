package com.sundaytools.app.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundaytools.app.database.entity.PlayerTagEntity
import com.sundaytools.app.repository.PlayerTagRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryDetailViewModel @Inject constructor(
    private val repository: PlayerTagRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val dayKey: String = savedStateHandle.get<String>("dayKey").orEmpty()

    private val _tags = MutableStateFlow<List<PlayerTagEntity>>(emptyList())
    val tags: StateFlow<List<PlayerTagEntity>> = _tags.asStateFlow()

    init {
        viewModelScope.launch {
            _tags.value = repository.getTagsForDay(dayKey)
        }
    }

    fun currentDayKey(): String = dayKey
}
