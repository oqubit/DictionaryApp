package com.example.dictionaryapp.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionaryapp.di.IoDispatcher
import com.example.dictionaryapp.domain.model.HistoryEntity
import com.example.dictionaryapp.domain.repository.DictionaryRepository
import com.example.dictionaryapp.domain.repository.SearchHistoryRepository
import com.example.dictionaryapp.domain.util.MyResult
import com.example.dictionaryapp.presentation.util.calcWordSimilarityScore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dictionaryRepo: DictionaryRepository,
    private val historyRepo: SearchHistoryRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
    }

    private val _mainState = MutableStateFlow(MainState())
    val mainState = _mainState.asStateFlow()

    private var searchJob: Job? = null

    init {
        Log.v(TAG, "ViewModel: Init ticked")
        onSearchClick(isVmInitCall = true)
    }

    private fun onSearchClick(isVmInitCall: Boolean = false) {
        Log.v(TAG, "Called: onSearchClick()")
        val searchedWord = mainState.value.searchWord
        if (searchedWord.isBlank()) {
            return
        }
        if (searchedWord.equals(mainState.value.lastSearchedWord, ignoreCase = true)) {
            return
        }
        searchJob?.cancel()
        searchJob = viewModelScope.launch(ioDispatcher) {
            historyRepo.addHistoryEntity(HistoryEntity(searchedWord))
            _mainState.update { state ->
                state.copy(
                    lastSearchedWord = searchedWord,
                    shouldReSortHistoryList = !isVmInitCall
                )
            }
            if (isVmInitCall) {
                updateAndResortSearchHistoryList()
            }
            searchWord()
        }
    }

    fun onEvent(eventArgs: MainEvents) {
        when (eventArgs) {
            MainEvents.OnSearchClick -> {
                Log.v(TAG, "Event: OnSearchClick ticked")
                onSearchClick()
            }

            is MainEvents.OnSearchWordChange -> {
                Log.v(TAG, "Event: OnSearchWordChange ticked")
                _mainState.update {
                    it.copy(searchWord = eventArgs.newWord)
                }
                if (eventArgs.reSortHistoryList) {
                    onEvent(MainEvents.ReSortSearchHistoryList)
                }
            }

            MainEvents.ReSortSearchHistoryList -> {
                Log.v(TAG, "Event: ReSortSearchHistoryList ticked")
                viewModelScope.launch(ioDispatcher) {
                    Log.v(TAG, "Called: ReSortSearchHistoryList")
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

            MainEvents.UpdateAndReSortSearchHistoryList -> {
                Log.v(TAG, "Event: UpdateAndReSortSearchHistoryList ticked")
                viewModelScope.launch(ioDispatcher) {
                    updateAndResortSearchHistoryList()
                }
            }
        }
    }

    private suspend fun updateAndResortSearchHistoryList() {
        Log.v(TAG, "Called: updateAndResortSearchHistoryList()")
        val searchHistoryList = historyRepo.getSearchHistoryList()
        _mainState.update { state ->
            state.copy(
                searchHistoryList = searchHistoryList.map { it.searchedWord }
            )
        }
        _mainState.update { state ->
            state.copy(
                shouldReSortHistoryList = false,
                searchHistoryList = state.searchHistoryList.sortedByDescending {
                    calcWordSimilarityScore(it, state.searchWord)
                }
            )
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
        Log.v(TAG, "Called: searchWord()")
        setPreWordLoadMainState()
        // delay(1000)
        dictionaryRepo.getWordResult(
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