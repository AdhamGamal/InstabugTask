package com.amg.instabugtask.database

import com.amg.instabugtask.models.Word

interface DatabaseUseCase {
    fun getWords(query: String? = null, sortOrder: String? = null): List<Word>
    fun insertWords(words: List<Word>): Int
    fun removeWords(): Int
}