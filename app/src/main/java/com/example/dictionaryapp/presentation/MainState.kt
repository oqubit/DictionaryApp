package com.example.dictionaryapp.presentation

import com.example.dictionaryapp.domain.model.WordItem

data class MainState (
    val isLoading: Boolean = true,
    val errorOccurred: Boolean = false,
    val showError: Boolean = false,
    val errorMessage: String = "",
    val searchWord: String = "Welcome",
    val wordItem: WordItem? = null
)