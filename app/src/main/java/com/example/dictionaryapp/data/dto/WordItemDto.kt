package com.example.dictionaryapp.data.dto

data class WordItemDto(
    val meanings: List<MeaningDto>? = null,
    val phonetics: List<PhoneticDto>? = null,
    val phonetic: String? = null,
    val word: String? = null
)