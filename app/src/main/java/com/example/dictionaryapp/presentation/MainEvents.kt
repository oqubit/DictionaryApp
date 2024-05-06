package com.example.dictionaryapp.presentation

sealed class MainEvents {
    data class OnSearchWordChange(val newWord: String, val reSortHistoryList: Boolean = true): MainEvents()
    data object OnSearchClick : MainEvents()
    data object UpdateAndReSortSearchHistoryList: MainEvents()
    data object ReSortSearchHistoryList: MainEvents()
}