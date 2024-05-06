package com.example.dictionaryapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.dictionaryapp.domain.model.HistoryEntity

@Dao
interface HistoryDao {
    @Upsert
    suspend fun addHistoryEntity(historyEntity: HistoryEntity)

    @Delete
    suspend fun deleteHistoryEntity(historyEntity: HistoryEntity)

    @Query("DELETE FROM search_history")
    suspend fun deleteSearchHistory()

    @Query("SELECT * FROM search_history")
    suspend fun getSearchHistoryList(): List<HistoryEntity>
}