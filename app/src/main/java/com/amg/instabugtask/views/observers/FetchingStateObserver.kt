package com.amg.instabugtask.views.observers

import androidx.lifecycle.Observer
import com.amg.instabugtask.views.MainActivityView
import com.amg.instabugtask.views.MainVM

class FetchingStateObserver(private val rootView: MainActivityView, private val mainVM: MainVM): Observer<FetchingState> {
    override fun onChanged(state: FetchingState) {
        rootView.onChange(state)
    }
}