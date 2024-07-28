package com.example.dictionaryapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.dictionaryapp.R
import com.example.dictionaryapp.presentation.MainEvents
import com.example.dictionaryapp.presentation.MainState

@Composable
fun WordAudioPlayButton(
    state: MainState,
    onEvent: (MainEvents) -> Unit
) {
    if (state.showError || !state.isAudioApiPresent || state.wordItem == null) {
        return
    }
    Box(
        modifier = Modifier
            .padding(horizontal = 30.dp)
            .padding(top = 10.dp)
            .border(
                5.dp,
                MaterialTheme.colorScheme.secondaryContainer.copy(0.7f),
                CircleShape
            )
            .background(
                MaterialTheme.colorScheme.secondaryContainer.copy(0.7f),
                CircleShape
            )
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary.copy(0.75f),
                            MaterialTheme.colorScheme.secondaryContainer,
                        )
                    ), CircleShape
                )
                .padding(18.dp)
                .clickable {
                    if (state.isLoading) {
                        return@clickable
                    }
                    onEvent(MainEvents.PlayAudio(state.wordItem.audioUrl))
                },
            contentAlignment = Alignment.Center
        ) {
            if (state.isAudioLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(22.dp)
                        .align(Alignment.Center),
                    color = Color.White
                )
                return
            }
            Icon(
                painter =
                if (state.isAudioPlaying)
                    painterResource(id = R.drawable.stop)
                else
                    painterResource(id = R.drawable.play),
                contentDescription = "Listen to pronunciation",
                modifier = Modifier
                    .size(22.dp),
                tint = Color.Unspecified
            )
        }
    }
}