package com.amg.instabugtask.database

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.SQLException
import android.net.Uri
import android.os.Build
import com.amg.instabugtask.database.WordsContract.WordEntry.BASE_URI
import com.amg.instabugtask.database.WordsContract.WordEntry.TABLE_NAME

class WordsProvider : ContentProvider() {

    private var dbHelper: WordsDbHelper? = null

    override fun onCreate(): Boolean {
        dbHelper = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WordsDbHelper(requireContext())
        } else {
            context?.let { WordsDbHelper(it) }
        }
        return true
    }


    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String?>?,
        sortOrder: String?
    ): Cursor? {

        val cursor = dbHelper?.readableDatabase?.query(
            TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
        )

        cursor?.apply {
            context?.contentResolver?.let {
                setNotificationUri(it, uri)
            }
        }
        return cursor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri {
        dbHelper?.writableDatabase?.insert(TABLE_NAME, null, values)?.let {
            if (it <= 0) {
                throw SQLException("Failed to insert row into $uri")
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                requireContext().contentResolver
            } else {
                context?.contentResolver
            }?.notifyChange(uri, null)

        }
        return BASE_URI
    }

    override fun bulkInsert(uri: Uri, values: Array<ContentValues>): Int {
        return dbHelper?.writableDatabase?.let {
            it.beginTransaction()
            try {
                for (cv in values) {
                    val newID = it.insertOrThrow(TABLE_NAME, null, cv)
                    if (newID <= 0) {
                        throw SQLException("Failed to insert row into $uri")
                    }
                }
                it.setTransactionSuccessful()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    requireContext().contentResolver
                } else {
                    context?.contentResolver
                }?.notifyChange(uri, null)
                values.size
            } finally {
                it.endTransaction()
            }
        } ?: 0
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String?>?): Int {
        return dbHelper?.writableDatabase?.delete(TABLE_NAME, selection, selectionArgs)?.let {
            if (it != 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    requireContext().contentResolver
                } else {
                    context?.contentResolver
                }?.notifyChange(uri, null)
            }
            it
        } ?: 0
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?
    ): Int {
        return dbHelper?.writableDatabase?.update(TABLE_NAME, values, selection, selectionArgs)
            ?.let {
                if (it != 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        requireContext().contentResolver
                    } else {
                        context?.contentResolver
                    }?.notifyChange(uri, null)
                }
                it
            } ?: 0
    }

    override fun getType(uri: Uri): String? = null
}