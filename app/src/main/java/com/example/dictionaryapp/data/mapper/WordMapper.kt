package com.example.dictionaryapp.data.mapper

import com.example.dictionaryapp.data.dto.DefinitionDto
import com.example.dictionaryapp.data.dto.MeaningDto
import com.example.dictionaryapp.data.dto.WordItemDto
import com.example.dictionaryapp.domain.model.Definition
import com.example.dictionaryapp.domain.model.Meaning
import com.example.dictionaryapp.domain.model.WordItem

fun WordItemDto.toWordItem() = WordItem (
    word = word?.replaceFirstChar(Char::titlecase) ?: "",
    meanings = meanings?.map {
        it.toMeaning()
    } ?: emptyList(),
    phonetic = phonetic ?: ""
)

fun MeaningDto.toMeaning() = Meaning(
    definition = definitions?.get(0).toDefinition(),
    partOfSpeech = partOfSpeech ?: ""
)

fun DefinitionDto?.toDefinition() = Definition(
    definition = this?.definition ?: "",
    example = this?.example ?: ""
)