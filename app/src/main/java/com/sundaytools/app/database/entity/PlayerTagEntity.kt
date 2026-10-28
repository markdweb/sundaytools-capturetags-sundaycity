package com.sundaytools.app.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A single player tag captured via OCR.
 *
 * [dayKey] stores the local calendar date the tag was captured on, formatted as
 * "yyyy-MM-dd". It is used to group rows into "Today" vs "History" and to detect
 * duplicates within the same day without needing a separate history table.
 *
 * [status] is "Blacklisted" or "Thrower", chosen when saving and editable
 * afterward from the Today's Tags table. [remoteId] is the row ID returned by
 * the Google Sheets backend once this entry has been successfully synced --
 * it's what lets a later status edit update the *same* row on the sheet
 * instead of creating a duplicate.
 */
@Entity(
    tableName = "player_tags",
    indices = [Index(value = ["tag", "dayKey"]), Index(value = ["dayKey"])]
)
data class PlayerTagEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String? = null,
    val tag: String,
    val savedAtEpochMillis: Long,
    val dayKey: String,
    val status: String = "Blacklisted",
    val reason: String = "",
    val remoteId: String? = null
)

/** "Name - Tag" when a name was captured, otherwise just the tag. Used anywhere this row's info is copied or displayed. */
val PlayerTagEntity.displayLabel: String
    get() = if (name != null) "$name - $tag" else tag
