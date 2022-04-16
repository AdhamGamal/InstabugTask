package com.amg.instabugtask.utils

import com.amg.instabugtask.models.Word
import java.util.Locale

object HtmlUtils {
    fun convertToWords(htmlPage: String): List<Word> {
        // Regex
        val tags = Regex("<[^>]*>")
        val specialChars = Regex("[^A-Za-z\'\\s]")
        val singleQuote = Regex("^\"|\"$")
        val spaces = Regex("[\\n\\t\\r\\s]+")

        // filters and sort
        val allWords = htmlPage.substringAfter("<body>")
            .replace(tags, " ")
            .replace(specialChars, "")
            .replace(spaces, " ")
            .split(" ")
            .filter { it.isNotEmpty() }
            .map { word ->
                word.replaceFirstChar {
                    if (it.isLowerCase()) {
                        it.titlecase(Locale.getDefault())
                    } else {
                        it.toString()
                    }
                }.replace(singleQuote, "")
            }
            .sorted()

        // sort and remove repeated words
        val words = allWords.toSortedSet().map { Word(it) }

        // algorithm
        var startIndex = 0
        val size = allWords.size
        words.map { word ->
            for (index in startIndex until size) {
                if (word.chars == allWords[index]) {
                    word.count++
                } else {
                    startIndex = index
                    break
                }
            }
        }

        return words
    }
}