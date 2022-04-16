package com.amg.instabugtask.views.listeners

interface ViewListener {
    fun onSearch(query: String)
    fun onCancelSearch()
    fun onSortDesc()
    fun onSortAsc()
    fun onRetry()
    fun onSortDefault()
}