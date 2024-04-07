package com.example.dictionaryapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionaryapp.domain.repository.DictionaryRepository
import com.example.dictionaryapp.domain.util.MyResult
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
        onEvent(MainUiEvents.OnSearchClick)
    }

    fun onEvent(mainUiEvent: MainUiEvents) {
        when (mainUiEvent) {
            MainUiEvents.OnSearchClick -> {
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    searchWord()
                }
            }

            is MainUiEvents.OnSearchWordChange -> {
                _mainState.update {
                    it.copy(
                        searchWord = mainUiEvent.newWord
                    )
                }
            }
        }
    }

    private fun setPreWordLoadMainState() {
        _mainState.update {
            it.copy(
                isLoading = true,
                errorOccurred = false
            )
        }
    }

    private suspend fun searchWord() {
        setPreWordLoadMainState()
        dictionaryRepository.getWordResult(
            mainState.value.searchWord.lowercase()
        ).collect { result ->
            when (result) {
                is MyResult.Error -> {
                    result.message?.let { err ->
                        _mainState.update {
                            it.copy(
                                errorOccurred = true,
                                errorMessage = err,
                                showError = true,
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