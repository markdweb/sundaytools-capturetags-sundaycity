package com.sundaytools.app.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sundaytools.app.database.entity.PlayerTagEntity
import com.sundaytools.app.database.entity.displayLabel
import com.sundaytools.app.utils.DateUtils

/**
 * A single row in the "Today's Tags" or History detail list, showing the tag,
 * its Blacklisted/Thrower status, the time it was saved, and Copy / Edit /
 * Delete actions. Edit opens a small dialog (see EditStatusDialog) to correct
 * the status after the fact -- useful since a tag is usually saved as
 * "Blacklisted" right away and only later confirmed as "Thrower" or vice versa.
 */
@Composable
fun TagRow(
    tag: PlayerTagEntity,
    onCopy: (String) -> Unit,
    onEdit: (PlayerTagEntity) -> Unit,
    onDelete: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = tag.displayLabel,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer()
                    StatusBadge(status = tag.status)
                }
                Text(
                    text = DateUtils.formatTime(tag.savedAtEpochMillis),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onCopy(tag.displayLabel) }) {
                    Icon(Icons.Filled.ContentCopy, contentDescription = "Copy name and tag")
                }
                IconButton(onClick = { onEdit(tag) }) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit status")
                }
                IconButton(onClick = { onDelete(tag.id) }) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Delete tag",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun Spacer() {
    androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
}

@Composable
private fun StatusBadge(status: String) {
    val color = if (status.equals("Thrower", ignoreCase = true)) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.error
    }
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Text(
            text = status,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}
