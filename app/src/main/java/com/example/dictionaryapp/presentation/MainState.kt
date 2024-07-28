package com.example.dictionaryapp.presentation

import android.media.MediaPlayer
import com.example.dictionaryapp.domain.model.WordItem

data class MainState (
    val wordItem: WordItem? = null,
    val mediaPlayer: MediaPlayer? = null,
    val searchWord: String = "Welcome",
    val lastSearchedWord: String = "",
    val wordToDelete: String = "",
    val errorMessage: String = "",
    val isLoading: Boolean = true,
    val isAudioLoading: Boolean = false,
    val isAudioPlaying: Boolean = false,
    val isAudioApiPresent: Boolean = false,
    val showError: Boolean = false,
    val showSearchHistoryDialogBox: Boolean = false,
    val shouldReSortHistoryList: Boolean = false,
    val searchHistoryList: List<String> = emptyList()
)