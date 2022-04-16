package com.amg.instabugtask.views.asyncTasks

import android.os.AsyncTask
import androidx.lifecycle.MutableLiveData
import com.amg.instabugtask.database.DatabaseUseCase
import com.amg.instabugtask.database.WordsContract.WordEntry.ORDER_ASC
import com.amg.instabugtask.database.WordsContract.WordEntry.ORDER_DESC
import com.amg.instabugtask.models.Word
import com.amg.instabugtask.views.observers.FetchingState


class FetchLocalWords(
    private val databaseUseCase: DatabaseUseCase,
    private val wordsLiveData: MutableLiveData<FetchingState>
) : AsyncTask<String?, Void, List<Word>>() {

    override fun doInBackground(vararg params: String?): List<Word> {
        wordsLiveData.postValue(FetchingState.Loading("Start Loading Words..."))

        val parameter = params[0]

        val words = when (parameter) {
            null -> {
                databaseUseCase.getWords()
            }
            ORDER_ASC -> {
                databaseUseCase.getWords(sortOrder = parameter)
            }
            ORDER_DESC -> {
                databaseUseCase.getWords(sortOrder = parameter)
            }
            else -> {
                databaseUseCase.getWords(query = parameter)
            }
        }

        if (words.isEmpty()) {
            if (parameter != null && parameter != ORDER_ASC && parameter != ORDER_DESC) {
                wordsLiveData.postValue(FetchingState.Failed("No Search Result!"))
            } else {
                wordsLiveData.postValue(FetchingState.Failed("No Internet Connections!"))
            }
        }
        return words
    }

    override fun onPostExecute(words: List<Word>) {
        if (words.isNotEmpty()) {
            wordsLiveData.value = FetchingState.Loaded(words)
        }
    }

    override fun onCancelled() {
        super.onCancelled()
        wordsLiveData.value = FetchingState.Failed("Something Went Wrong!")
    }
}