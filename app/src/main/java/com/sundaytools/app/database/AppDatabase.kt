package com.sundaytools.app.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sundaytools.app.database.dao.PlayerTagDao
import com.sundaytools.app.database.entity.PlayerTagEntity

@Database(
    entities = [PlayerTagEntity::class],
    version = 3,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playerTagDao(): PlayerTagDao

    companion object {
        const val DATABASE_NAME = "sundaytools.db"
    }
}
