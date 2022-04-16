package com.amg.instabugtask.database.impl

import android.content.ContentResolver
import android.content.ContentValues
import android.util.Log
import com.amg.instabugtask.database.DatabaseUseCase
import com.amg.instabugtask.database.WordsContract.WordEntry.BASE_URI
import com.amg.instabugtask.database.WordsContract.WordEntry.COLUMN_NAME_CHARS
import com.amg.instabugtask.database.WordsContract.WordEntry.COLUMN_NAME_COUNT
import com.amg.instabugtask.models.Word
import org.json.JSONException


class DatabaseUseCaseImpl(private val contentResolver: ContentResolver?) : DatabaseUseCase {

    override fun getWords(query: String?, sortOrder: String?): List<Word> {
        val columns = arrayOf(COLUMN_NAME_CHARS, COLUMN_NAME_COUNT)
        val words = mutableListOf<Word>()

        var selectionClause: String? = null
        val selectionArgs = query?.takeIf { it.isNotEmpty() }?.let {
            selectionClause = "$COLUMN_NAME_CHARS LIKE ?"
            arrayOf("%$it%")
        } ?: run {
            emptyArray()
        }

        val cursor = contentResolver?.query(
            BASE_URI,
            columns,
            selectionClause,
            selectionArgs,
            sortOrder
        )

        cursor?.let {
            if (it.count > 0) {
                if (it.moveToFirst()) {
                    try {
                        do {
                            words.add(Word(it.getString(0), it.getInt(1)))
                        } while (it.moveToNext())
                    } catch (e: JSONException) {
                        Log.e(this.javaClass.name, e.message.toString())
                    }
                }
            }
        }
        cursor?.close()

        return words
    }

    val failed = 0

    override fun insertWords(words: List<Word>): Int {
        return if (words.isNotEmpty()) {
            try {
                val contentValues = words.map {
                    ContentValues().apply {
                        put(COLUMN_NAME_CHARS, it.chars)
                        put(COLUMN_NAME_COUNT, it.count)
                    }
                }
                contentResolver?.bulkInsert(BASE_URI, contentValues.toTypedArray()) ?: failed
            } catch (e: Exception) {
                Log.e(this.javaClass.name, e.message.toString())
                failed
            }
        } else failed
    }

    override fun removeWords(): Int {
        return contentResolver?.delete(BASE_URI, null, null) ?: failed
    }
}