package com.example.dictionaryapp.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class HistoryEntity(
    @PrimaryKey val searchedWord: String = "NULL",
    val timestamp: Long
)
