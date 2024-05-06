package com.example.dictionaryapp.presentation

import android.content.res.Configuration
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dictionaryapp.R
import com.example.dictionaryapp.domain.model.Definition
import com.example.dictionaryapp.domain.model.Meaning
import com.example.dictionaryapp.domain.model.WordItem
import com.example.dictionaryapp.presentation.util.keyboardAsState
import com.example.dictionaryapp.ui.theme.DictionaryAppTheme

@Composable
fun MainScreen(
    state: MainState,
    onEvent: (MainEvents) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(vertical = 5.dp, horizontal = 16.dp)
                    .focusRequester(focusRequester),
                value = state.searchWord,
                onValueChange = {
                    onEvent(
                        MainEvents.OnSearchWordChange(it)
                    )
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onEvent(MainEvents.OnSearchClick)
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                ),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = stringResource(R.string.search_a_word),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(30.dp)
                            .clickable {
                                onEvent(MainEvents.OnSearchClick)
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }
                    )
                },
                label = {
                    Text(
                        text = stringResource(R.string.search_a_word),
                        fontSize = 15.sp,
                        modifier = Modifier.alpha(0.7f)
                    )
                },
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 19.sp
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            WordScreen(state)
            HistoryList(
                searchHistoryList = state.searchHistoryList,
                shouldResortHistory = state.shouldReSortHistoryList,
                onEvent = onEvent,
                keyboardController = keyboardController,
                focusManager = focusManager
            )
        }
    }
}

@Composable
fun HistoryList(
    searchHistoryList: List<String>,
    shouldResortHistory: Boolean,
    onEvent: (MainEvents) -> Unit,
    keyboardController: SoftwareKeyboardController?,
    focusManager: FocusManager
) {
    val isKeyboardOpen by keyboardAsState()
    val animVisibleState = remember { MutableTransitionState(false) }

    LaunchedEffect(isKeyboardOpen) {
        animVisibleState.targetState = isKeyboardOpen
    }

    val animationEnded = animVisibleState.isIdle && !animVisibleState.currentState
    if (animationEnded && shouldResortHistory) {
        Log.v("MainScreen", "UI-Tick: OnBoxCloseAnimationEnd ticked!")
        onEvent(MainEvents.UpdateAndReSortSearchHistoryList)
    }

    AnimatedVisibility(
        visibleState = animVisibleState,
        enter = expandVertically(
            expandFrom = Alignment.Top,
            animationSpec = spring(stiffness = Spring.StiffnessMedium)
        ) + fadeIn(
            initialAlpha = 0.2f,
            animationSpec = spring(stiffness = Spring.StiffnessMedium)
        ),
        exit = shrinkVertically() + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .padding(
                    horizontal = 16.dp
                )
                .clip(RoundedCornerShape(7.dp))
                .background(color = MaterialTheme.colorScheme.background)
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.secondaryContainer.copy(0.2f)
                        )
                    )
                )
                .border(
                    border = BorderStroke(
                        width = 3.dp,
                        brush = Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.background,
                                MaterialTheme.colorScheme.primary.copy(0.3f)
                            )
                        )
                    ),
                    shape = RoundedCornerShape(7.dp)
                )
        ) {
            Spacer(modifier = Modifier.height(5.dp))
            LazyColumn {
                items(
                    items = searchHistoryList.take(5),
                    key = { it }
                ) { item ->
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 17.dp, vertical = 7.dp)
                            .fillMaxWidth()
                            .clickable {
                                onEvent(MainEvents.OnSearchWordChange(item, false))
                                onEvent(MainEvents.OnSearchClick)
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            modifier = Modifier.padding(end = 8.dp),
                            contentDescription = ""
                        )
                        Text(
                            text = item,
                            fontSize = 19.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WordScreen(
    state: MainState
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(horizontal = 30.dp)
        ) {
            WordResultTitle(state = state)
        }
        Box(
            modifier = Modifier
                .padding(top = 90.dp)
                .fillMaxSize()
                .clip(
                    RoundedCornerShape(
                        topStart = 50.dp,
                        topEnd = 50.dp
                    )
                )
                .background(
                    if (state.showError)
                        MaterialTheme.colorScheme.background
                    else
                        MaterialTheme.colorScheme.secondaryContainer.copy(0.7f)
                )
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            } else if (!state.showError) {
                state.wordItem?.let { wordItem ->
                    WordResultBody(wordItem)
                }
            }
        }
    }
}

@Composable
fun WordResultTitle(
    state: MainState
) {
    state.wordItem?.let { wordItem ->
        WordResultTitleText(
            mainStr = if (state.showError) stringResource(R.string.hmm) else wordItem.word,
            secondaryStr = if (state.showError) state.errorMessage else wordItem.phonetic,
        )
    }
    if (state.showError && state.wordItem == null) {
        WordResultTitleText(
            mainStr = stringResource(R.string.oops),
            secondaryStr = stringResource(R.string.unable_to_reach_the_server)
        )
    }
}

@Composable
fun WordResultTitleText(
    mainStr: String,
    secondaryStr: String
) {
    Spacer(modifier = Modifier.height(10.dp))
    Text(
        text = mainStr,
        fontSize = 30.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(5.dp))
    Text(
        text = secondaryStr,
        fontSize = 17.sp,
        color = MaterialTheme.colorScheme.onBackground
    )
    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
fun WordResultBody(wordItem: WordItem) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 32.dp)
    ) {
        items(wordItem.meanings.size) { index ->
            WordMeaning(
                meaning = wordItem.meanings[index],
                index = index
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun WordMeaning(
    meaning: Meaning,
    index: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "${index + 1}. ${meaning.partOfSpeech}",
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(0.4f),
                            Color.Transparent
                        )
                    )
                )
                .padding(
                    top = 2.dp,
                    bottom = 4.dp,
                    start = 12.dp,
                    end = 12.dp
                )
        )
        WordInfo(
            infoName = stringResource(R.string.definition),
            infoText = meaning.definition.definition
        )
        WordInfo(
            infoName = stringResource(R.string.example),
            infoText = meaning.definition.example
        )
    }
}

@Composable
fun WordInfo(
    infoName: String,
    infoText: String
) {
    if (infoText.isEmpty()) {
        return
    }
    Spacer(modifier = Modifier.height(8.dp))
    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = infoName,
            fontWeight = FontWeight.SemiBold,
            fontSize = 19.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = infoText,
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}


data class PreviewParams(
    val isLoading: Boolean,
    val showError: Boolean
)

class MyPreviewParamsProvider : PreviewParameterProvider<PreviewParams> {
    override val values: Sequence<PreviewParams>
        get() = sequenceOf(
            PreviewParams(isLoading = false, showError = false),
            PreviewParams(isLoading = false, showError = true),
            // PreviewParams(isLoading = true, showError = false),
            // PreviewParams(isLoading = true, showError = true)
        )
}

@Preview(
    fontScale = 1f,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
// @PreviewScreenSizes
// @PreviewFontScale
// @PreviewLightDark
@Composable
private fun MainScreenPreview(
    @PreviewParameter(MyPreviewParamsProvider::class) data: PreviewParams
) {
    DictionaryAppTheme {
        MainScreen(
            onEvent = {},
            state = MainState(
                isLoading = data.isLoading,
                showError = data.showError,
                errorMessage = stringResource(R.string.couldnt_find_this_word),
                wordItem = WordItem(
                    word = "Welcome",
                    phonetic = "/ˈwɛlkəm/",
                    meanings = listOf(
                        Meaning(
                            partOfSpeech = "noun",
                            definition = Definition(
                                definition = "The act of greeting someone’s arrival, " +
                                        "especially by saying \"Welcome!\"; reception.",
                                example = ""
                            )
                        ),
                        Meaning(
                            partOfSpeech = "verb",
                            definition = Definition(
                                definition = "To affirm or greet the arrival of someone, " +
                                        "especially by saying \"Welcome!\".",
                                example = ""
                            )
                        ),
                        Meaning(
                            partOfSpeech = "adjective",
                            definition = Definition(
                                definition = "Whose arrival is a cause of joy; " +
                                        "received with gladness; admitted willingly to the " +
                                        "house, entertainment, or company.",
                                example = "Refugees welcome in London!"
                            )
                        ),
                        Meaning(
                            partOfSpeech = "interjection",
                            definition = Definition(
                                definition = "Greeting given upon someone's arrival.",
                                example = ""
                            )
                        )
                    )
                )
            )
        )
    }
}