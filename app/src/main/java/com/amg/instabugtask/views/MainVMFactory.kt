package com.amg.instabugtask.views

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.amg.instabugtask.database.DatabaseUseCase
import com.amg.instabugtask.network.NetworkUseCase


class MainVMFactory(
    private val networkUseCase: NetworkUseCase,
    private val databaseUseCase: DatabaseUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainVM(networkUseCase, databaseUseCase) as T
    }
}

