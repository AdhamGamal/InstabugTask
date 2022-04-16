package com.amg.instabugtask.views.asyncTasks

import android.os.AsyncTask
import androidx.lifecycle.MutableLiveData
import com.amg.instabugtask.database.DatabaseUseCase
import com.amg.instabugtask.models.Word
import com.amg.instabugtask.network.NetworkUseCase
import com.amg.instabugtask.utils.HtmlUtils.convertToWords
import com.amg.instabugtask.views.observers.FetchingState


class FetchWords(
    private val networkUseCase: NetworkUseCase,
    private val databaseUseCase: DatabaseUseCase,
    private val wordsLiveData: MutableLiveData<FetchingState>
) : AsyncTask<Void, Void, List<Word>>() {

    override fun doInBackground(vararg p0: Void?): List<Word> {
        wordsLiveData.postValue(FetchingState.Loading("Start Fetching Words..."))
        val htmlPage = networkUseCase.getHtml()
        wordsLiveData.postValue(FetchingState.Loading("Count Words Appearance..."))
        val words = htmlPage?.let {
            convertToWords(htmlPage)
        } ?: emptyList()

        if (words.isEmpty()) {
            val localWords = databaseUseCase.getWords()
            if (localWords.isEmpty()) {
                wordsLiveData.postValue(FetchingState.Failed("No Internet Connections..."))
            }
            return localWords
        } else {
            wordsLiveData.postValue(FetchingState.Loading("Saving Words..."))
            val isDeleted = databaseUseCase.removeWords()
            wordsLiveData.postValue(FetchingState.Loading("Almost Done Words..."))
            val isInserted = databaseUseCase.insertWords(words)
            if (isInserted == 0) {
                wordsLiveData.postValue(FetchingState.Failed("Saving Failed..."))
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
        wordsLiveData.value = FetchingState.Failed("Something Went Wrong.")
    }
}