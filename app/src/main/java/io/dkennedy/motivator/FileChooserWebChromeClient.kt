package io.dkennedy.motivator

import android.net.Uri
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.activity.result.ActivityResultLauncher

class FileChooserWebChromeClient(
    private val importFileChooser: ActivityResultLauncher<Array<String>>
) : WebChromeClient() {
    var filePathCallback: ValueCallback<Array<Uri>>? = null

    override fun onConsoleMessage(message: ConsoleMessage): Boolean {
        Log.d(TAG, "${message.message()} at ${message.lineNumber()}:${message.sourceId()}")
        return true
    }

    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        this.filePathCallback = filePathCallback
        importFileChooser.launch(arrayOf("application/json"))
        return true
    }

    companion object {
        private const val TAG = "io.dkennedy.Motivator"
    }
}
