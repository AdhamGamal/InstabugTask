package com.amg.instabugtask.views.observers

import com.amg.instabugtask.models.Word

sealed class FetchingState {
    class Loading(val message: String) : FetchingState()
    class Loaded(val words: List<Word>) : FetchingState()
    class Failed(val message: String) : FetchingState()
}
