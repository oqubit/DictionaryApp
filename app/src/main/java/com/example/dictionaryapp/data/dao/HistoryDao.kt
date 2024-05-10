package com.example.dictionaryapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.example.dictionaryapp.domain.model.HistoryEntity

@Dao
interface HistoryDao {
    @Upsert
    suspend fun addHistoryEntity(historyEntity: HistoryEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun initHistoryEntity(historyEntity: HistoryEntity)

    @Delete
    suspend fun deleteHistoryEntity(historyEntity: HistoryEntity)

    @Query("DELETE FROM search_history")
    suspend fun deleteSearchHistory()

    @Query("SELECT * FROM search_history")
    suspend fun getSearchHistoryList(): List<HistoryEntity>

    @Query("SELECT * FROM search_history ORDER BY timestamp DESC")
    suspend fun getSearchHistoryListByRecent(): List<HistoryEntity>
}