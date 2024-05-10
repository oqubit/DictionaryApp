package com.example.dictionaryapp.data.repository

import com.example.dictionaryapp.data.dao.HistoryDao
import com.example.dictionaryapp.domain.model.HistoryEntity
import com.example.dictionaryapp.domain.repository.SearchHistoryRepository
import javax.inject.Inject

class SearchHistoryRepositoryImpl @Inject constructor(
    private val dao: HistoryDao
): SearchHistoryRepository {
    override suspend fun addHistoryEntity(historyEntity: HistoryEntity) {
        dao.addHistoryEntity(historyEntity)
    }

    override suspend fun initHistoryEntity(historyEntity: HistoryEntity) {
        dao.initHistoryEntity(historyEntity)
    }

    override suspend fun deleteHistoryEntity(historyEntity: HistoryEntity) {
        dao.deleteHistoryEntity(historyEntity)
    }

    override suspend fun deleteSearchHistory() {
        dao.deleteSearchHistory()
    }

    override suspend fun getSearchHistoryList(): List<HistoryEntity> {
        return dao.getSearchHistoryList()
    }

    override suspend fun getSearchHistoryListByRecent(): List<HistoryEntity> {
        return dao.getSearchHistoryListByRecent()
    }
}