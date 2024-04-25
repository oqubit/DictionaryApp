package com.example.dictionaryapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionaryapp.domain.repository.DictionaryRepository
import com.example.dictionaryapp.domain.util.MyResult
import com.example.dictionaryapp.presentation.util.calcWordSimilarityScore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dictionaryRepository: DictionaryRepository
) : ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
    }

    private val _mainState = MutableStateFlow(MainState())
    val mainState = _mainState.asStateFlow()

    private var searchJob: Job? = null

    init {
        onEvent(MainEvents.OnSearchClick())
        onEvent(MainEvents.ReSortHistoryList)
    }

    fun onEvent(eventArgs: MainEvents) {
        when (eventArgs) {
            is MainEvents.OnSearchClick -> {
                val searchedWord = mainState.value.searchWord
                if (searchedWord.isBlank()) {
                    return
                }
                if (searchedWord.equals(mainState.value.lastSearchedWord, ignoreCase = true)) {
                    return
                }
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    searchWord()
                }
                _mainState.update {
                    it.copy(
                        lastSearchedWord = searchedWord,
                        shouldReSortHistoryList = eventArgs.shouldReSortHistoryListLater
                    )
                }
            }

            is MainEvents.OnSearchWordChange -> {
                _mainState.update {
                    it.copy(searchWord = eventArgs.newWord)
                }
                if (eventArgs.reSortHistoryList) {
                    onEvent(MainEvents.ReSortHistoryList)
                }
            }

            MainEvents.ReSortHistoryList -> {
                viewModelScope.launch {
                    _mainState.update { state ->
                        state.copy(
                            shouldReSortHistoryList = false,
                            searchHistoryList = state.searchHistoryList.sortedByDescending {
                                calcWordSimilarityScore(it, state.searchWord)
                            }
                        )
                    }
                }
            }
        }
    }

    private fun setPreWordLoadMainState() {
        _mainState.update {
            it.copy(
                isLoading = true
            )
        }
    }

    private suspend fun searchWord() {
        setPreWordLoadMainState()
        // delay(800)
        dictionaryRepository.getWordResult(
            mainState.value.searchWord.lowercase()
        ).collect { result ->
            when (result) {
                is MyResult.Error -> {
                    result.message?.let { err ->
                        _mainState.update {
                            it.copy(
                                showError = true,
                                errorMessage = err,
                                isLoading = false
                            )
                        }
                    }
                }

                is MyResult.Success -> {
                    result.data?.let { wordItem ->
                        _mainState.update {
                            it.copy(
                                wordItem = wordItem,
                                showError = false,
                                isLoading = false
                            )
                        }
                    }
                }
            }
        }
    }
}