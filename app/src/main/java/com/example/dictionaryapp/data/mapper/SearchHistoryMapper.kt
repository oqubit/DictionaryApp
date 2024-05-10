package com.example.dictionaryapp.data.mapper

import com.example.dictionaryapp.domain.model.HistoryEntity

fun List<HistoryEntity>.toStringList() = (this.map { it.searchedWord })
