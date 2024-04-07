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
    definition = definitionDtoToDefinition(definitions?.get(0)),
    partOfSpeech = partOfSpeech ?: ""
)

fun definitionDtoToDefinition(
    definitionDto: DefinitionDto?
) = Definition (
    definition = definitionDto?.definition ?: "",
    example = definitionDto?.example ?: ""
)