package com.example.dictionaryapp.presentation

sealed class MainEvents {
    data class OnSearchWordChange(val newWord: String, val reSortHistoryList: Boolean = true): MainEvents()
    data class OnSearchClick(val shouldReSortHistoryListLater: Boolean = false): MainEvents()
    data object ReSortHistoryList: MainEvents()
}