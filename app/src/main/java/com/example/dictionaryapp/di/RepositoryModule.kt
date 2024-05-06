package com.example.dictionaryapp.di

import com.example.dictionaryapp.data.repository.DictionaryRepositoryImpl
import com.example.dictionaryapp.data.repository.SearchHistoryRepositoryImpl
import com.example.dictionaryapp.domain.repository.DictionaryRepository
import com.example.dictionaryapp.domain.repository.SearchHistoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindDictionaryRepository(
        dictionaryRepositoryImpl: DictionaryRepositoryImpl
    ): DictionaryRepository

    @Binds
    abstract fun bindSearchHistoryRepository(
        searchHistoryRepositoryImpl: SearchHistoryRepositoryImpl
    ): SearchHistoryRepository
}