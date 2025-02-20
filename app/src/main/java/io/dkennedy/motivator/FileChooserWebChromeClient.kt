package io.dkennedy.motivator

import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.activity.result.ActivityResultLauncher

class FileChooserWebChromeClient(
    private val importFileChooser: ActivityResultLauncher<Array<String>>
) : WebChromeClient() {
    var filePathCallback: ValueCallback<Array<Uri>>? = null

    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        this.filePathCallback = filePathCallback
        importFileChooser.launch(arrayOf("application/json"))
        return true
    }
}