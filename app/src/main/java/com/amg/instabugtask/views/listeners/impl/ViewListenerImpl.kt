package com.amg.instabugtask.views.listeners.impl

import com.amg.instabugtask.database.WordsContract.WordEntry.ORDER_ASC
import com.amg.instabugtask.database.WordsContract.WordEntry.ORDER_DESC
import com.amg.instabugtask.views.MainVM
import com.amg.instabugtask.views.listeners.ViewListener

class ViewListenerImpl(private val mainVM: MainVM) : ViewListener {
    override fun onSearch(query: String) {
        mainVM.search(query)
    }

    override fun onCancelSearch() {
        mainVM.cancelSearch()
    }

    override fun onSortAsc() {
        mainVM.sort(ORDER_ASC)
    }

    override fun onSortDesc() {
        mainVM.sort(ORDER_DESC)
    }

    override fun onRetry() {
        mainVM.onRetry()
    }

    override fun onSortDefault() {
        mainVM.sort(null)
    }
}