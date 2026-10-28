package com.sundaytools.app.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sundaytools.app.database.entity.PlayerTagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerTagDao {

    @Insert
    suspend fun insert(tag: PlayerTagEntity): Long

    /** All tags saved on [dayKey], newest first. Used for the "Today's Tags" screen. */
    @Query("SELECT * FROM player_tags WHERE dayKey = :dayKey ORDER BY savedAtEpochMillis DESC")
    fun observeTagsForDay(dayKey: String): Flow<List<PlayerTagEntity>>

    /** Instant (non-Flow) read, used by the overlay service and workers. */
    @Query("SELECT * FROM player_tags WHERE dayKey = :dayKey ORDER BY savedAtEpochMillis DESC")
    suspend fun getTagsForDay(dayKey: String): List<PlayerTagEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM player_tags WHERE dayKey = :dayKey AND tag = :tag)")
    suspend fun existsForDay(dayKey: String, tag: String): Boolean

    /** Whether [tag] has ever been saved before, on any day -- used for blacklist warnings. */
    @Query("SELECT EXISTS(SELECT 1 FROM player_tags WHERE tag = :tag)")
    suspend fun existsAnywhere(tag: String): Boolean

    @Query("DELETE FROM player_tags WHERE id = :id")
    suspend fun deleteById(id: Long)

    /** Distinct calendar days that have at least one saved tag, most recent first. */
    @Query("SELECT DISTINCT dayKey FROM player_tags ORDER BY dayKey DESC")
    fun observeAllDayKeys(): Flow<List<String>>

    @Query("SELECT COUNT(*) FROM player_tags WHERE dayKey = :dayKey")
    suspend fun countForDay(dayKey: String): Int

    @Query("SELECT * FROM player_tags ORDER BY savedAtEpochMillis DESC")
    suspend fun getAllTags(): List<PlayerTagEntity>

    @Query("SELECT * FROM player_tags WHERE dayKey = :dayKey AND tag LIKE '%' || :query || '%' ORDER BY savedAtEpochMillis DESC")
    fun searchTagsForDay(dayKey: String, query: String): Flow<List<PlayerTagEntity>>

    /** Lets the user correct the status (Blacklisted/Thrower) and reason after saving. */
    @Query("UPDATE player_tags SET status = :status, reason = :reason WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String, reason: String)

    /** Records the Google Sheets row ID once a captured entry has been synced, so later edits update that same row. */
    @Query("UPDATE player_tags SET remoteId = :remoteId WHERE id = :id")
    suspend fun updateRemoteId(id: Long, remoteId: String)

    @Query("SELECT * FROM player_tags WHERE id = :id")
    suspend fun getById(id: Long): PlayerTagEntity?
}
