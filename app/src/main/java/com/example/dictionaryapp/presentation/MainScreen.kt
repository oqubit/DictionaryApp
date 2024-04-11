package com.example.dictionaryapp.presentation

import android.content.res.Configuration
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dictionaryapp.R
import com.example.dictionaryapp.domain.model.Definition
import com.example.dictionaryapp.domain.model.Meaning
import com.example.dictionaryapp.domain.model.WordItem
import com.example.dictionaryapp.ui.theme.DictionaryAppTheme

@Composable
fun MainScreen(
    state: MainState,
    onEvent: (MainUiEvents) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            LaunchedEffect(key1 = state.errorOccurred) {
                if (state.errorOccurred) {
                    keyboardController?.show()
                    focusRequester.requestFocus()
                }
            }
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(vertical = 5.dp, horizontal = 16.dp)
                    .focusRequester(focusRequester),
                value = state.searchWord,
                onValueChange = {
                    onEvent(
                        MainUiEvents.OnSearchWordChange(it)
                    )
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onEvent(MainUiEvents.OnSearchClick)
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
                                onEvent(MainUiEvents.OnSearchClick)
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
            MyScreen(state)
        }
    }
}

@Composable
fun MyScreen(
    state: MainState
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(horizontal = 30.dp)
        ) {
            state.wordItem?.let { wordItem ->
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = if (state.showError) stringResource(R.string.hmm) else wordItem.word,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (state.showError) state.errorMessage else wordItem.phonetic,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        Box(
            modifier = Modifier
                .padding(top = 110.dp)
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
                    WordResult(wordItem)
                }
            }
        }
    }
}

@Composable
fun WordResult(wordItem: WordItem) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 32.dp)
    ) {
        items(wordItem.meanings.size) { index ->
            Meaning(
                meaning = wordItem.meanings[index],
                index = index
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun Meaning(
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
class MyPreviewParamsProvider: PreviewParameterProvider<PreviewParams> {
    override val values: Sequence<PreviewParams>
        get() = sequenceOf(
            PreviewParams(isLoading = false, showError = false),
            PreviewParams(isLoading = false, showError = true),
            PreviewParams(isLoading = true, showError = false),
            PreviewParams(isLoading = true, showError = true)
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