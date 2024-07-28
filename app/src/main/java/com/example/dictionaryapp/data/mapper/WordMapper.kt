package com.example.dictionaryapp.data.mapper

import com.example.dictionaryapp.data.dto.DefinitionDto
import com.example.dictionaryapp.data.dto.MeaningDto
import com.example.dictionaryapp.data.dto.PhoneticDto
import com.example.dictionaryapp.data.dto.WordItemDto
import com.example.dictionaryapp.domain.model.Definition
import com.example.dictionaryapp.domain.model.Meaning
import com.example.dictionaryapp.domain.model.WordItem

fun WordItemDto.toWordItem() = WordItem(
    word = word?.replaceFirstChar(Char::titlecase) ?: "",
    meanings = meanings?.map {
        it.toMeaning()
    } ?: emptyList(),
    phonetic = phonetic ?: phonetics.toPhoneticString(),
    audioUrl = phonetics.toPhoneticAudioUrl()
)

fun MeaningDto.toMeaning() = Meaning(
    definition = definitions?.get(0).toDefinition(),
    partOfSpeech = partOfSpeech ?: ""
)

fun DefinitionDto?.toDefinition() = Definition(
    definition = this?.definition ?: "",
    example = this?.example ?: ""
)

fun List<PhoneticDto>?.toPhoneticString(): String {
    return this?.find { !it.text.isNullOrEmpty() }?.text ?: ""
}

fun List<PhoneticDto>?.toPhoneticAudioUrl(): String {
    return this?.find { !it.audio.isNullOrEmpty() }?.audio ?: ""
}