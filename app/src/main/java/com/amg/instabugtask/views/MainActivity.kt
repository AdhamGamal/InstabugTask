package com.amg.instabugtask.views

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.amg.instabugtask.database.impl.DatabaseUseCaseImpl
import com.amg.instabugtask.network.impl.NetworkUseCaseImpl
import com.amg.instabugtask.views.listeners.impl.ViewListenerImpl
import com.amg.instabugtask.views.observers.FetchingStateObserver

class MainActivity : AppCompatActivity() {

    private val mainVM: MainVM by viewModels {
        MainVMFactory(
            NetworkUseCaseImpl(),
            DatabaseUseCaseImpl(contentResolver)
        )
    }

    private val rootView: MainActivityView = MainActivityViewImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = rootView.onInflate(layoutInflater)
        setContentView(view)
        setSupportActionBar(rootView.getToolbar())

        mainVM.handleConfigurationChanges()

        val inputMethodManager =
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        rootView.setup(inputMethodManager, ViewListenerImpl(mainVM))

        mainVM.observeFetchingState(this, FetchingStateObserver(rootView))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("ListState", rootView.getListState())

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        rootView.setListState(savedInstanceState.getParcelable("ListState"))
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        rootView.onInflateMenu(menuInflater, menu, searchManager, componentName)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        rootView.onMenuItemSelected(item.itemId)
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        rootView.onDispose()
    }
}