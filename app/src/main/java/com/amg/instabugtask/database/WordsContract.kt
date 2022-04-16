package com.amg.instabugtask.database

import android.content.UriMatcher
import android.net.Uri
import android.provider.BaseColumns


object WordsContract {

    object WordEntry : BaseColumns {
        const val TABLE_NAME = "WordsTable"
        const val COLUMN_NAME_CHARS = "word_chars"
        const val COLUMN_NAME_COUNT = "word_count"
        const val AUTHORITY = "com.amg.instabugtask"
        const val ORDER_ASC = "$COLUMN_NAME_COUNT ASC"
        const val ORDER_DESC = "$COLUMN_NAME_COUNT DESC"
        val BASE_URI: Uri = Uri.parse("content://$AUTHORITY/$TABLE_NAME")

        const val BASE_CODE = 1
        const val ID_CODE = 2

        val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, TABLE_NAME, BASE_CODE)
            addURI(AUTHORITY, "$TABLE_NAME/#", ID_CODE)
        }
    }
}