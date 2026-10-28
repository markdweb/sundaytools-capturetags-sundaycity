package com.sundaytools.app.ui.screen

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.sundaytools.app.export.ExportFormat
import com.sundaytools.app.export.TagExporter
import com.sundaytools.app.ui.component.TagRow
import com.sundaytools.app.utils.ClipboardUtils
import com.sundaytools.app.viewmodel.HistoryDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDetailScreen(
    dayKey: String,
    onBack: () -> Unit,
    viewModel: HistoryDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val tags by viewModel.tags.collectAsState()
    var menuExpanded by remember { mutableStateOf(false) }
    val exporter = remember { TagExporter() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(dayKey) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Filled.Share, contentDescription = "Export")
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text("Export as CSV") },
                            onClick = {
                                menuExpanded = false
                                shareExport(context, exporter, tags, ExportFormat.CSV)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Export as TXT") },
                            onClick = {
                                menuExpanded = false
                                shareExport(context, exporter, tags, ExportFormat.TXT)
                            }
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (tags.isEmpty()) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No tags recorded for this day.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(tags, key = { it.id }) { tag ->
                    TagRow(
                        tag = tag,
                        onCopy = { text -> ClipboardUtils.copyToClipboard(context, text) },
                        onEdit = { /* History rows are read-only by design */ },
                        onDelete = { /* History rows are read-only by design */ }
                    )
                }
            }
        }
    }
}

private fun shareExport(
    context: android.content.Context,
    exporter: TagExporter,
    tags: List<com.sundaytools.app.database.entity.PlayerTagEntity>,
    format: ExportFormat
) {
    val uri = exporter.export(context, tags, format)
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = if (format == ExportFormat.CSV) "text/csv" else "text/plain"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(shareIntent, "Export tags"))
}
