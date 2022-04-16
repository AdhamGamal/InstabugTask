package com.amg.instabugtask.database

import android.net.Uri
import android.provider.BaseColumns


object WordsContract {

    object WordEntry : BaseColumns {
        const val TABLE_NAME = "WordsTable"
        const val COLUMN_NAME_CHARS = "word_chars"
        const val COLUMN_NAME_COUNT = "word_count"
        private const val AUTHORITY = "com.amg.instabugtask"
        const val ORDER_ASC = "$COLUMN_NAME_COUNT ASC"
        const val ORDER_DESC = "$COLUMN_NAME_COUNT DESC"
        val BASE_URI: Uri = Uri.parse("content://$AUTHORITY/$TABLE_NAME")
    }
}