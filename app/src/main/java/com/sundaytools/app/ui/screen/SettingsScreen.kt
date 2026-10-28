package com.sundaytools.app.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sundaytools.app.viewmodel.SettingsViewModel

private data class SettingToggle(
    val title: String,
    val subtitle: String,
    val checked: Boolean,
    val onToggle: (Boolean) -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsState()

    val toggles = listOf(
        SettingToggle(
            title = "Auto Copy",
            subtitle = "Copy detected tags to clipboard automatically",
            checked = settings.autoCopyEnabled,
            onToggle = viewModel::setAutoCopy
        ),
        SettingToggle(
            title = "Vibrate after Scan",
            subtitle = "Short vibration when a tag is detected",
            checked = settings.vibrateOnScan,
            onToggle = viewModel::setVibrateOnScan
        ),
        SettingToggle(
            title = "Sound after Scan",
            subtitle = "Play a confirmation tone when a tag is detected",
            checked = settings.soundOnScan,
            onToggle = viewModel::setSoundOnScan
        ),
        SettingToggle(
            title = "Dark Mode",
            subtitle = "Use a dark color theme throughout the app",
            checked = settings.darkMode,
            onToggle = viewModel::setDarkMode
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                CloudSyncRow(
                    enabled = settings.cloudSyncEnabled,
                    backendUrl = settings.backendUrl,
                    addedByName = settings.addedByName,
                    onToggle = viewModel::setCloudSyncEnabled,
                    onSaveUrl = viewModel::setBackendUrl,
                    onSaveAddedBy = viewModel::setAddedByName
                )
            }
            items(toggles) { toggle ->
                SettingToggleRow(toggle)
            }
            item {
                ArchiveTimeRow(
                    hour = settings.archiveHour,
                    minute = settings.archiveMinute,
                    onChange = viewModel::setArchiveTime
                )
            }
            item {
                CaptureAreaRow(
                    hasCaptureRegion = settings.hasCaptureRegion,
                    onReset = viewModel::clearCaptureRegion
                )
            }
            item {
                BlacklistTemplateRow(
                    template = settings.blacklistWarningTemplate,
                    onChange = viewModel::setBlacklistWarningTemplate
                )
            }
        }
    }
}

/**
 * Lets the user customize the message copied by "Copy Warning" on the
 * Blacklist Warning popup. Supports {name} and {tag} placeholders.
 */
@Composable
private fun BlacklistTemplateRow(template: String, onChange: (String) -> Unit) {
    var text by androidx.compose.runtime.remember(template) {
        androidx.compose.runtime.mutableStateOf(template)
    }
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text("Blacklist Warning Message", fontWeight = FontWeight.SemiBold)
        Text(
            "Copied when you tap \"Copy Warning\". Use {name} and {tag} as placeholders.",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        androidx.compose.material3.OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
        androidx.compose.material3.TextButton(
            onClick = { onChange(text) },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Save Message")
        }
    }
}

/**
 * Configures the Google Sheets sync: the deployed Apps Script Web App "exec"
 * URL (same backend the web dashboard uses) and the label recorded in the
 * sheet's "Added By" column for tags captured from this device.
 */
@Composable
private fun CloudSyncRow(
    enabled: Boolean,
    backendUrl: String,
    addedByName: String,
    onToggle: (Boolean) -> Unit,
    onSaveUrl: (String) -> Unit,
    onSaveAddedBy: (String) -> Unit
) {
    var urlText by androidx.compose.runtime.remember(backendUrl) {
        androidx.compose.runtime.mutableStateOf(backendUrl)
    }
    var addedByText by androidx.compose.runtime.remember(addedByName) {
        androidx.compose.runtime.mutableStateOf(addedByName)
    }

    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.padding(end = 12.dp)) {
                Text("Google Sheets Sync", fontWeight = FontWeight.SemiBold)
                Text(
                    "Push captured tags into the same Blacklist sheet the web app uses",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(checked = enabled, onCheckedChange = onToggle)
        }

        androidx.compose.material3.OutlinedTextField(
            value = urlText,
            onValueChange = { urlText = it },
            label = { Text("Apps Script Web App URL (ends in /exec)") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        )
        androidx.compose.material3.OutlinedTextField(
            value = addedByText,
            onValueChange = { addedByText = it },
            label = { Text("Added By (shown in the sheet's Added By column)") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        )
        androidx.compose.material3.TextButton(
            onClick = {
                onSaveUrl(urlText)
                onSaveAddedBy(addedByText)
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Save Sync Settings")
        }
    }
}

/**
 * Shows whether a custom OCR capture area is configured. The area itself is
 * set via the "Set Capture Area" button in the floating bubble's popup menu
 * (it needs to appear over the game to be drawn), so this row is just status
 * + a way to reset back to scanning the whole screen.
 */
@Composable
private fun CaptureAreaRow(hasCaptureRegion: Boolean, onReset: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.padding(end = 12.dp)) {
            Text("Capture Area", fontWeight = FontWeight.SemiBold)
            Text(
                if (hasCaptureRegion) {
                    "Scanning a custom area (set from the bubble menu)"
                } else {
                    "Scanning the whole screen. Set an area from the bubble's popup menu for more accurate results."
                },
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (hasCaptureRegion) {
            androidx.compose.material3.TextButton(onClick = onReset) {
                Text("Reset")
            }
        }
    }
}

@Composable
private fun SettingToggleRow(toggle: SettingToggle) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.padding(end = 12.dp)) {
            Text(toggle.title, fontWeight = FontWeight.SemiBold)
            Text(
                toggle.subtitle,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = toggle.checked, onCheckedChange = toggle.onToggle)
    }
}

/**
 * Lets the user pick the daily archive time (default 11:00 PM). Uses the
 * platform TimePickerDialog via an AndroidView-free approach: a simple button
 * that opens the system dialog, keeping this screen fully Compose otherwise.
 */
@Composable
private fun ArchiveTimeRow(hour: Int, minute: Int, onChange: (Int, Int) -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val displayHour = if (hour % 12 == 0) 12 else hour % 12
    val amPm = if (hour < 12) "AM" else "PM"
    val timeLabel = String.format("%d:%02d %s", displayHour, minute, amPm)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Archive Time", fontWeight = FontWeight.SemiBold)
            Text(
                "Tags automatically move to History at this time each day",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        androidx.compose.material3.TextButton(onClick = {
            android.app.TimePickerDialog(
                context,
                { _, pickedHour, pickedMinute -> onChange(pickedHour, pickedMinute) },
                hour,
                minute,
                false
            ).show()
        }) {
            Text(timeLabel)
        }
    }
}
