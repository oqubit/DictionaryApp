package com.example.dictionaryapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.dictionaryapp.data.dao.HistoryDao
import com.example.dictionaryapp.domain.model.HistoryEntity

@Database(
    entities = [HistoryEntity::class],
    version = 1
)
abstract class SearchHistoryDatabase: RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    companion object {
        const val DATABASE_NAME = "search_history.db"
    }
}