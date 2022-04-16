package com.amg.instabugtask.views

import android.app.SearchManager
import android.content.ComponentName
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
//import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import com.amg.instabugtask.R
import com.amg.instabugtask.databinding.ActivityMainBinding
import com.amg.instabugtask.views.adapters.WordsAdapter
import com.amg.instabugtask.views.listeners.ViewListener
import com.amg.instabugtask.views.observers.FetchingState


class MainActivityViewImpl : MainActivityView {

    private var binding: ActivityMainBinding? = null
    private var inputMethodManager: InputMethodManager? = null
    private var viewListener: ViewListener? = null
    private var listState: Parcelable? = null
    private var stateChanged = true

    private val views = listOf<View?>(
        binding?.layoutLoading?.container,
        binding?.layoutError?.container,
        binding?.wordsList,
    )

    override fun onInflate(layoutInflater: LayoutInflater): View? {
        binding = ActivityMainBinding.inflate(layoutInflater)
        return binding?.root
    }

    override fun setup(inputMethodManager: InputMethodManager, viewListener: ViewListener) {
        binding?.let {
            this.inputMethodManager = inputMethodManager
            this.viewListener = viewListener
            it.wordsList.addItemDecoration(
                DividerItemDecoration(it.root.context, DividerItemDecoration.VERTICAL)
            )
            it.layoutError.retry.setOnClickListener {
                viewListener.onRetry()
            }
        }
    }

    override fun getListState(): Parcelable? {
        return binding?.wordsList?.layoutManager?.onSaveInstanceState()
    }

    override fun setListState(state: Parcelable?) {
        stateChanged = true
        listState = state
    }


    override fun onInflateMenu(
        menuInflater: MenuInflater,
        menu: Menu,
        searchManager: SearchManager,
        componentName: ComponentName,
    ) {

        menuInflater.inflate(R.menu.menu_main, menu)

        val searchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setIconifiedByDefault(true)
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.isNotEmpty()) {
                    hideKeyboard(searchView)
                    viewListener?.onSearch(query)
                }
                return true
            }
        })
        searchView.setOnCloseListener {
            hideKeyboard(searchView)
            viewListener?.onCancelSearch()
            false
        }
    }

    override fun onMenuItemSelected(itemId: Int) {
        when (itemId) {
            R.id.action_sort_asc -> {
                viewListener?.onSortAsc()
            }
            R.id.action_sort_desc -> {
                viewListener?.onSortDesc()
            }
            R.id.action_sort_default -> {
                viewListener?.onSortDefault()
            }
            else -> {}
        }
    }


    override fun onChange(state: FetchingState) {
        binding?.let {
            when (state) {
                is FetchingState.Loading -> {
                    it.layoutLoading.message.text = state.message
                    setVisible(it.layoutLoading.container)
                }
                is FetchingState.Loaded -> {
                    it.wordsList.adapter = WordsAdapter(state.words).apply {
//                        stateRestorationPolicy = PREVENT_WHEN_EMPTY
                    }
                    if (stateChanged) {
                        binding?.wordsList?.layoutManager?.onRestoreInstanceState(listState)
                        stateChanged = false
                    }
                    setVisible(it.wordsList)
                }
                is FetchingState.Failed -> {
                    it.layoutError.message.text = state.message
                    setVisible(it.layoutError.container)
                }
            }
        }
    }

    override fun getToolbar(): Toolbar? {
        return binding?.toolbar
    }

    private fun setVisible(view: View) {
        views.forEach {
            it?.let {
                if (it == view) {
                    it.visibility = View.VISIBLE
                } else {
                    it.visibility = View.GONE
                }
            }
        }
    }

    private fun hideKeyboard(view: View) {
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }


    override fun onDispose() {
        binding = null
        inputMethodManager = null
        viewListener = null
        listState = null
    }
}