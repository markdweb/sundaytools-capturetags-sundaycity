package com.sundaytools.app.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sundaytools.app.network.RemoteBlacklistEntry
import com.sundaytools.app.ui.component.TagSearchBar
import com.sundaytools.app.viewmodel.CloudRecordsViewModel
import kotlinx.coroutines.delay

/** How often the screen re-polls the sheet while it's open and visible. */
private const val AUTO_REFRESH_INTERVAL_MS = 15_000L

/**
 * Shows every record currently in the shared Google Sheets "Blacklist" tab --
 * including entries added from the web app or another device -- so nothing
 * has to be manually re-entered on Android. Auto-refreshes periodically
 * while open, since the Apps Script backend can't push updates to the app.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloudRecordsScreen(
    onBack: () -> Unit,
    viewModel: CloudRecordsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val query by viewModel.searchQuery.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refresh()
        while (true) {
            delay(AUTO_REFRESH_INTERVAL_MS)
            viewModel.refresh()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cloud Records") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh now")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            SyncStatusRow(
                isLoading = uiState.isLoading,
                backendConfigured = uiState.backendConfigured,
                errorMessage = uiState.errorMessage,
                entryCount = uiState.entries.size
            )

            if (uiState.backendConfigured) {
                TagSearchBar(query = query, onQueryChange = viewModel::onSearchQueryChanged)
            }

            when {
                !uiState.backendConfigured -> {
                    EmptyState(
                        title = "Sync not set up yet",
                        subtitle = "Add your Apps Script Web App URL in Settings \u2192 Google Sheets Sync to see records here."
                    )
                }
                uiState.isLoading && uiState.entries.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                uiState.entries.isEmpty() -> {
                    EmptyState(
                        title = "No records yet",
                        subtitle = "Records added from the web app or any device will show up here automatically."
                    )
                }
                else -> {
                    val filtered = viewModel.filteredEntries()
                    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
                        items(filtered, key = { it.id ?: it.tag }) { entry ->
                            CloudRecordCard(entry)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SyncStatusRow(
    isLoading: Boolean,
    backendConfigured: Boolean,
    errorMessage: String?,
    entryCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (backendConfigured && errorMessage == null) Icons.Filled.CloudDone else Icons.Filled.CloudOff,
                contentDescription = null,
                tint = if (backendConfigured && errorMessage == null) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                },
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = when {
                    !backendConfigured -> "Not connected"
                    errorMessage != null -> "Sync error"
                    else -> "$entryCount records synced"
                },
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 6.dp)
            )
        }
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
        }
    }
}

@Composable
private fun EmptyState(title: String, subtitle: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        Text(
            subtitle,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 6.dp, start = 24.dp, end = 24.dp)
        )
    }
}

@Composable
private fun CloudRecordCard(entry: RemoteBlacklistEntry) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (!entry.name.isNullOrBlank()) "${entry.name} - ${entry.tag}" else entry.tag,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                if (!entry.status.isNullOrBlank()) {
                    StatusBadge(entry.status)
                }
            }
            if (!entry.reason.isNullOrBlank()) {
                Text(
                    text = entry.reason,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = entry.dateAdded.orEmpty(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = entry.addedBy?.let { "Added by $it" }.orEmpty(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(status: String) {
    val color = if (status.equals("Thrower", ignoreCase = true)) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.error
    }
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Text(
            text = status,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
