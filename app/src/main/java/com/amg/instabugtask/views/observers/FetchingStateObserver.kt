package com.amg.instabugtask.views.observers

import androidx.lifecycle.Observer
import com.amg.instabugtask.views.MainActivityView

class FetchingStateObserver(private val rootView: MainActivityView): Observer<FetchingState> {
    override fun onChanged(state: FetchingState) {
        rootView.onChange(state)
    }
}