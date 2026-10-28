package com.sundaytools.app.repository

import com.sundaytools.app.database.dao.PlayerTagDao
import com.sundaytools.app.database.entity.PlayerTagEntity
import com.sundaytools.app.utils.DateUtils
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

sealed class SaveTagResult {
    data class Saved(val entity: PlayerTagEntity) : SaveTagResult()
    object AlreadySavedToday : SaveTagResult()
}

@Singleton
class PlayerTagRepository @Inject constructor(
    private val dao: PlayerTagDao
) {

    fun observeTodayTags(): Flow<List<PlayerTagEntity>> =
        dao.observeTagsForDay(DateUtils.todayKey())

    fun searchTodayTags(query: String): Flow<List<PlayerTagEntity>> =
        dao.searchTagsForDay(DateUtils.todayKey(), query)

    fun observeHistoryDayKeys(): Flow<List<String>> = dao.observeAllDayKeys()

    suspend fun getTagsForDay(dayKey: String): List<PlayerTagEntity> = dao.getTagsForDay(dayKey)

    suspend fun getAllTags(): List<PlayerTagEntity> = dao.getAllTags()

    suspend fun getById(id: Long): PlayerTagEntity? = dao.getById(id)

    /**
     * Whether [tag] has ever been saved before, on any day. Scanning a tag
     * that's already on record means this player was flagged before, so the
     * overlay shows a Blacklist Warning instead of the normal save flow.
     */
    suspend fun isBlacklisted(tag: String): Boolean = dao.existsAnywhere(tag)

    /**
     * Saves [name] + [tag] + [status] for today unless [tag] was already
     * saved today. Callers should check [isBlacklisted] first if they want to
     * warn about tags seen on *any* previous day, not just today.
     */
    suspend fun saveTag(name: String?, tag: String, status: String = "Blacklisted", reason: String = ""): SaveTagResult {
        val dayKey = DateUtils.todayKey()
        if (dao.existsForDay(dayKey, tag)) {
            return SaveTagResult.AlreadySavedToday
        }
        val entity = PlayerTagEntity(
            name = name,
            tag = tag,
            savedAtEpochMillis = System.currentTimeMillis(),
            dayKey = dayKey,
            status = status,
            reason = reason
        )
        val id = dao.insert(entity)
        return SaveTagResult.Saved(entity.copy(id = id))
    }

    suspend fun deleteTag(id: Long) = dao.deleteById(id)

    /** Corrects a saved entry's status (e.g. "Blacklisted" was picked but it should be "Thrower"). */
    suspend fun updateStatus(id: Long, status: String, reason: String) = dao.updateStatus(id, status, reason)

    /** Remembers which Google Sheets row this entry synced to, so a later status edit updates it in place. */
    suspend fun setRemoteId(id: Long, remoteId: String) = dao.updateRemoteId(id, remoteId)
}
