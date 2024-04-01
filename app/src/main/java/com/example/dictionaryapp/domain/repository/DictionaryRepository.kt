package com.example.dictionaryapp.domain.repository

import com.example.dictionaryapp.domain.model.WordItem
import com.example.dictionaryapp.domain.util.MyResult
import kotlinx.coroutines.flow.Flow

interface DictionaryRepository {
    suspend fun getWordResult(
        word: String
    ): Flow<MyResult<WordItem>>
}