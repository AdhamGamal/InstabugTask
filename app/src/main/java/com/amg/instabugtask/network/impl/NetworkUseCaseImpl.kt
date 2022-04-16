package com.amg.instabugtask.network.impl

import android.util.Log
import com.amg.instabugtask.network.NetworkUseCase
import java.net.HttpURLConnection
import java.net.URL

class NetworkUseCaseImpl : NetworkUseCase {

    override fun getHtml(): String? {
        return try {
            val url = URL("https://instabug.com")
            val urlConnection = url.openConnection() as HttpURLConnection
            val text = urlConnection.inputStream.bufferedReader().readText()
            urlConnection.disconnect()
            text
        } catch (e: Exception) {
            Log.e(this.javaClass.name, e.message.toString())
            null
        }
    }
}