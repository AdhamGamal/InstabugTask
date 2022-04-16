package com.amg.instabugtask.views

import android.os.AsyncTask
import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.amg.instabugtask.database.DatabaseUseCase
import com.amg.instabugtask.network.NetworkUseCase
import com.amg.instabugtask.views.asyncTasks.FetchLocalWords
import com.amg.instabugtask.views.asyncTasks.FetchWords
import com.amg.instabugtask.views.observers.FetchingState

class MainVM(
    private val networkUseCase: NetworkUseCase,
    private val databaseUseCase: DatabaseUseCase
) : ViewModel() {

    private val wordsLiveData = MutableLiveData<FetchingState>(FetchingState.Loading("Loading"))
    private var fetchingWordsTask: FetchWords? = null
    private var fetchingLocalWordsTask: FetchLocalWords? = null
    private var searchQuery = ""
    private var sortOrder = ""

    fun handleConfigurationChanges() {
        when {
            searchQuery.isNotEmpty() -> {
                search(searchQuery)
            }
            sortOrder.isNotEmpty() -> {
                sort(sortOrder)
            }
            else -> {
                getWords()
            }
        }
    }

    fun observeFetchingState(lifecycleOwner: LifecycleOwner, observer: Observer<FetchingState>) {
        wordsLiveData.observe(lifecycleOwner, observer)
    }

    fun onRetry() {
        updateStateChanges("", "")
        getWords()
    }

    fun search(query: String) {
        updateStateChanges(query, "")
        getLocalWords(query)
    }

    fun cancelSearch() {
        updateStateChanges("", "")
        getLocalWords(null)
    }

    fun sort(order: String?) {
        updateStateChanges("", order ?: "")
        getLocalWords(order)
    }

    private fun clearTasks() {
        fetchingWordsTask?.cancel(true)
        fetchingLocalWordsTask?.cancel(true)
    }

    private fun updateStateChanges(query: String, order: String) {
        searchQuery = query
        sortOrder = order
    }

    private fun getWords() {
        if (fetchingWordsTask?.status != AsyncTask.Status.FINISHED || fetchingLocalWordsTask?.status != AsyncTask.Status.FINISHED) {
            clearTasks()
        }
        fetchingWordsTask = FetchWords(networkUseCase, databaseUseCase, wordsLiveData)
        fetchingWordsTask?.execute()
    }

    private fun getLocalWords(parameter: String?) {
        if (fetchingWordsTask?.status != AsyncTask.Status.FINISHED || fetchingLocalWordsTask?.status != AsyncTask.Status.FINISHED) {
            clearTasks()
        }
        fetchingLocalWordsTask = FetchLocalWords(databaseUseCase, wordsLiveData)
        fetchingLocalWordsTask?.execute(parameter)
    }

    override fun onCleared() {
        super.onCleared()
        clearTasks()
    }
}