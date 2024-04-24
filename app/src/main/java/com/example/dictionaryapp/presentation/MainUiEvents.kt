package com.example.dictionaryapp.presentation

sealed class MainUiEvents {
    data class OnSearchWordChange(val newWord: String, val reSortHistoryList: Boolean = true): MainUiEvents()
    data class OnSearchClick(val shouldReSortHistoryListLater: Boolean = false): MainUiEvents()
    data object ReSortHistoryList: MainUiEvents()
}