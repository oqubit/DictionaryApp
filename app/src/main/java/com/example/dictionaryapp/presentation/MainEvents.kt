package com.example.dictionaryapp.presentation

sealed class MainEvents {
    data class OnSearchWordChange(val newWord: String, val reSortHistoryList: Boolean = true): MainEvents()
    data object OnSearchClick : MainEvents()
    data object UpdateAndReSortSearchHistoryList: MainEvents()
    data object ReSortSearchHistoryList: MainEvents()
    data class OnSearchHistoryLongPressOpen(val wordToDelete: String) : MainEvents()
    data class OnSearchHistoryLongPressClose(val canDelete: Boolean = false): MainEvents()
    data class PlayAudio(val audioUrl: String) : MainEvents()
}