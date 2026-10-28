package com.sundaytools.app.ui.screen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sundaytools.app.database.entity.PlayerTagEntity
import com.sundaytools.app.export.ExportFormat
import com.sundaytools.app.export.TagExporter
import com.sundaytools.app.ui.component.EditStatusDialog
import com.sundaytools.app.ui.component.HomeHeroCard
import com.sundaytools.app.ui.component.TagRow
import com.sundaytools.app.ui.component.TagSearchBar
import com.sundaytools.app.utils.ClipboardUtils
import com.sundaytools.app.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenCloudRecords: () -> Unit,
    onRequestOverlay: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val tags by viewModel.todayTags.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    var exportMenuExpanded by remember { mutableStateOf(false) }
    var editingTag by remember { mutableStateOf<PlayerTagEntity?>(null) }
    val exporter = remember { TagExporter() }
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("SundayTools", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { exportMenuExpanded = true }) {
                        Icon(Icons.Filled.Share, contentDescription = "Export all tags")
                    }
                    DropdownMenu(
                        expanded = exportMenuExpanded,
                        onDismissRequest = { exportMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Export all as CSV") },
                            onClick = {
                                exportMenuExpanded = false
                                coroutineScope.launch {
                                    val all = viewModel.exportAllTags()
                                    shareExportedTags(context, exporter, all, ExportFormat.CSV)
                                }
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Export all as TXT") },
                            onClick = {
                                exportMenuExpanded = false
                                coroutineScope.launch {
                                    val all = viewModel.exportAllTags()
                                    shareExportedTags(context, exporter, all, ExportFormat.TXT)
                                }
                            }
                        )
                    }
                    IconButton(onClick = onOpenCloudRecords) {
                        Icon(Icons.Filled.Cloud, contentDescription = "Cloud Records")
                    }
                    IconButton(onClick = onOpenHistory) {
                        Icon(Icons.Filled.History, contentDescription = "History")
                    }
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            HomeHeroCard(todayCount = tags.size)

            Spacer(modifier = Modifier.size(16.dp))

            Button(
                onClick = {
                    onRequestOverlay()
                    showBubbleStartedToast(context)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Start Floating Bubble")
            }

            Spacer(modifier = Modifier.size(12.dp))

            TagSearchBar(query = query, onQueryChange = viewModel::onSearchQueryChanged)

            Spacer(modifier = Modifier.size(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Tags",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "${tags.size} saved",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (tags.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "No tags saved yet today.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Tap the bubble over the game to capture one.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
                    items(tags, key = { it.id }) { tag ->
                        TagRow(
                            tag = tag,
                            onCopy = { text ->
                                ClipboardUtils.copyToClipboard(context, text)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Copied: $text")
                                }
                            },
                            onEdit = { editingTag = it },
                            onDelete = { id ->
                                viewModel.deleteTag(id)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Tag deleted")
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    editingTag?.let { tag ->
        EditStatusDialog(
            tag = tag,
            onSave = { status, reason ->
                viewModel.updateStatus(tag, status, reason)
                editingTag = null
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Status updated: $status")
                }
            },
            onDismiss = { editingTag = null }
        )
    }
}

private fun showBubbleStartedToast(context: Context) {
    Toast.makeText(context, "Bubble permission requested / started", Toast.LENGTH_SHORT).show()
}

private fun shareExportedTags(
    context: Context,
    exporter: TagExporter,
    tags: List<com.sundaytools.app.database.entity.PlayerTagEntity>,
    format: ExportFormat
) {
    val uri = exporter.export(context, tags, format)
    val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
        type = if (format == ExportFormat.CSV) "text/csv" else "text/plain"
        putExtra(android.content.Intent.EXTRA_STREAM, uri)
        addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(android.content.Intent.createChooser(shareIntent, "Export tags"))
}
