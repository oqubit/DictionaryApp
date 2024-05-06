package com.example.dictionaryapp.domain.repository

import com.example.dictionaryapp.domain.model.HistoryEntity

interface SearchHistoryRepository {
    suspend fun addHistoryEntity(historyEntity: HistoryEntity)
    suspend fun deleteHistoryEntity(historyEntity: HistoryEntity)
    suspend fun deleteSearchHistory()
    suspend fun getSearchHistoryList(): List<HistoryEntity>
}