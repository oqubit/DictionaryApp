package com.example.dictionaryapp.presentation

import com.example.dictionaryapp.domain.model.WordItem

data class MainState (
    val wordItem: WordItem? = null,
    val searchWord: String = "Welcome",
    val lastSearchedWord: String = "",
    val errorMessage: String = "",
    val isLoading: Boolean = true,
    val showError: Boolean = false
)