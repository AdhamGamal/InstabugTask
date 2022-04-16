package com.amg.instabugtask.views

import android.app.SearchManager
import android.content.ComponentName
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.Toolbar
import com.amg.instabugtask.views.listeners.ViewListener
import com.amg.instabugtask.views.listeners.impl.ViewListenerImpl
import com.amg.instabugtask.views.observers.FetchingState

interface MainActivityView {

    fun onInflate(layoutInflater: LayoutInflater): View?
    fun onInflateMenu(
        menuInflater: MenuInflater,
        menu: Menu,
        searchManager: SearchManager,
        componentName: ComponentName
    )
    fun setup(inputMethodManager: InputMethodManager, viewListener: ViewListener)
    fun getListState(): Parcelable?
    fun setListState(state: Parcelable?)
    fun onMenuItemSelected(itemId: Int)
    fun onChange(state: FetchingState)
    fun getToolbar(): Toolbar?
    fun onDispose()
}