package com.example.dictionaryapp.presentation.util

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Compute "listStr" similarity score based on the "searchedStr" o_Q
 */
fun calcWordSimilarityScore(listStr: String, searchedStr: String): Int {
    // Consider match distance to avoid pitfalls like this:
    // Search: "gg" Get: ["Greetings", "Doggo"] (bad)
    // With distance:    ["Doggo", "Greetings"] (correct)
    var score = 0
    var idxDist = 0
    var matches = 0
    var distMod = 0 // Makes sure to always give max score on first match
    var matchedFirstChar = false
    val strSearched = searchedStr.lowercase()
    val strFromList = listStr.lowercase().toCharArray()
    for (i in strSearched.indices) {
        for (j in strFromList.indices) {
            if (strSearched[i] == strFromList[j]) {
                if (j == 0) {
                    score++ // This avoids a very peculiar edge case o_Q
                    matchedFirstChar = true
                }
                val dist = abs(j - idxDist) * distMod
                score += max(10 - dist, 1)
                strFromList[j] = '\u0000'
                idxDist = j + 1
                distMod = 1
                matches++
                break
            }
        }
    }
    // Favor smaller matching strings first:
    // Add at most 15 points based on the matching chars ratio.
    // Skew upwards if the searchWord matched the first listStr char.
    // Add extra points on fully matching char counts.
    if (matches > 0) {
        val strFromListSize = strFromList.size
        val skew = if (matchedFirstChar) 6f else 0f
        val scoreF = matches.toFloat() / strFromListSize.toFloat() * 15f + skew
        score += min(scoreF.toInt(), 15)
        if (matches == strFromListSize) {
            score += 8
        }
    }
    return score
}