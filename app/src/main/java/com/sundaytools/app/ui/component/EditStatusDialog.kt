package com.sundaytools.app.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sundaytools.app.database.entity.PlayerTagEntity

private val STATUS_OPTIONS = listOf("Blacklisted", "Thrower")

/**
 * Lets the user correct a saved tag's status/reason after the fact -- since a
 * tag is usually saved as "Blacklisted" right away and only confirmed as
 * "Thrower" (or vice versa) later on.
 */
@Composable
fun EditStatusDialog(
    tag: PlayerTagEntity,
    onSave: (status: String, reason: String) -> Unit,
    onDismiss: () -> Unit
) {
    var status by remember(tag.id) { mutableStateOf(tag.status) }
    var reason by remember(tag.id) { mutableStateOf(tag.reason) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(tag.displayLabelOrTag()) },
        text = {
            Column {
                Text(
                    "Status",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    STATUS_OPTIONS.forEach { option ->
                        val selected = option == status
                        Button(
                            onClick = { status = option },
                            modifier = Modifier.weight(1f),
                            colors = if (selected) {
                                ButtonDefaults.buttonColors()
                            } else {
                                ButtonDefaults.outlinedButtonColors()
                            }
                        ) {
                            Text(option)
                        }
                    }
                }
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Reason (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSave(status, reason) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun PlayerTagEntity.displayLabelOrTag(): String =
    if (name != null) "$name - $tag" else tag
