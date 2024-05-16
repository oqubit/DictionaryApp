package com.example.dictionaryapp.presentation

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dictionaryapp.data.mapper.toStringList
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

    override fun onCleared() {
        super.onCleared()
        releaseMediaPlayer()
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
        releaseMediaPlayer()
        searchJob?.cancel()
        searchJob = viewModelScope.launch(ioDispatcher) {
            _mainState.update { state ->
                state.copy(
                    lastSearchedWord = searchedWord,
                    shouldReSortHistoryList = !isVmInitCall
                )
            }
            val historyEntity = HistoryEntity(searchedWord, System.currentTimeMillis())
            if (!isVmInitCall) {
                historyRepo.addHistoryEntity(historyEntity)
            } else {
                historyRepo.initHistoryEntity(historyEntity)
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
                            searchHistoryList =
                            if (state.searchWord.isNotEmpty()) {
                                state.searchHistoryList.sortedByDescending {
                                    calcWordSimilarityScore(it, state.searchWord)
                                }
                            } else {
                                historyRepo.getSearchHistoryListByRecent().toStringList()
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

            is MainEvents.OnSearchHistoryLongPressOpen -> {
                Log.v(TAG, "Event: OnSearchHistoryLongPressOpen ticked")
                _mainState.update {
                    it.copy(
                        showSearchHistoryDialogBox = true,
                        wordToDelete = eventArgs.wordToDelete
                    )
                }
            }

            is MainEvents.OnSearchHistoryLongPressClose -> {
                Log.v(TAG, "Event: OnSearchHistoryLongPressClose ticked")
                viewModelScope.launch(ioDispatcher) {
                    if (eventArgs.canDelete) {
                        Log.v(TAG, "Called: Delete a search history item")
                        historyRepo.deleteHistoryEntity(
                            HistoryEntity(mainState.value.wordToDelete, 0)
                        )
                        updateAndResortSearchHistoryList()
                    }
                    _mainState.update {
                        it.copy(
                            showSearchHistoryDialogBox = false,
                            wordToDelete = ""
                        )
                    }
                }
            }

            is MainEvents.PlayAudio -> {
                // Create MediaPlayer if mediaPlayer variable is null
                _mainState.value.mediaPlayer ?: run {
                    Log.v(TAG, "MediaPlayer: CREATE")
                    val audioAttributes = AudioAttributes
                        .Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                    _mainState.update { it.copy(mediaPlayer = MediaPlayer()) }
                    _mainState.value.mediaPlayer?.apply {
                        setAudioAttributes(audioAttributes)
                        setDataSource(eventArgs.audioUrl)
                    }
                }

                // Play/Stop sound while controlling audio state variables
                _mainState.value.mediaPlayer?.apply {
                    Log.v(TAG, "MediaPlayer: Audio ID $audioSessionId")
                    if (_mainState.value.isAudioPlaying) {
                        Log.v(TAG, "MediaPlayer: STOP")
                        stop()
                        _mainState.update {
                            it.copy(
                                isAudioLoading = false,
                                isAudioPlaying = false
                            )
                        }
                        return@apply
                    }
                    if (!this.isPlaying && !_mainState.value.isAudioLoading) {
                        Log.v(TAG, "MediaPlayer: prepareAsync called")
                        prepareAsync()
                        _mainState.update {
                            it.copy(
                                isAudioLoading = true
                            )
                        }
                    }
                    setOnPreparedListener {
                        Log.v(TAG, "MediaPlayer: OnPrepared START")
                        start()
                        _mainState.update {
                            it.copy(
                                isAudioLoading = false,
                                isAudioPlaying = true
                            )
                        }
                    }
                    setOnCompletionListener {
                        Log.v(TAG, "MediaPlayer: OnCompleted STOP")
                        stop()
                        _mainState.update {
                            it.copy(
                                isAudioLoading = false,
                                isAudioPlaying = false
                            )
                        }
                    }
                }
            }
        }
    }

    private fun releaseMediaPlayer() {
        _mainState.value.mediaPlayer?.let {
            Log.v(TAG, "MediaPlayer: RELEASE")
            _mainState.value.mediaPlayer?.release()
            _mainState.update {
                it.copy(
                    mediaPlayer = null,
                    isAudioLoading = false,
                    isAudioPlaying = false
                )
            }
        }
    }

    private suspend fun updateAndResortSearchHistoryList() {
        Log.v(TAG, "Called: updateAndResortSearchHistoryList()")
        _mainState.update { state ->
            state.copy(
                searchHistoryList = historyRepo.getSearchHistoryList().toStringList()
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