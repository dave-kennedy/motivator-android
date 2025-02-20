package io.dkennedy.motivator

import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.webkit.WebViewAssetLoader
import io.dkennedy.motivator.databinding.ActivityMainBinding
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.net.URLDecoder

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var exportData: String? = null

    private val exportFileChooser: ActivityResultLauncher<String> = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            writeFile(uri)
        }
    }

    private val importFileChooser: ActivityResultLauncher<Array<String>> = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            webChromeClient.filePathCallback?.onReceiveValue(arrayOf(uri))
        }
    }

    private val webChromeClient = FileChooserWebChromeClient(importFileChooser)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.webView.settings.domStorageEnabled = true
        binding.webView.settings.javaScriptEnabled = true

        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .addPathHandler("/res/", WebViewAssetLoader.ResourcesPathHandler(this))
            .build()

        binding.webView.webViewClient = LocalContentWebViewClient(assetLoader)
        binding.webView.webChromeClient = webChromeClient

        binding.webView.setDownloadListener { url, _, _, _, _ ->
            val dataUrl = URLDecoder.decode(url, "UTF-8")
            exportData = dataUrl.replaceFirst("data:application/json;charset=UTF-8,", "")
            exportFileChooser.launch("motivator.json")
        }

        binding.webView.loadUrl("https://appassets.androidplatform.net/assets/motivator/index.html")
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && binding.webView.canGoBack()) {
            binding.webView.goBack()
            return true
        }

        return super.onKeyDown(keyCode, event)
    }

    private fun writeFile(uri: Uri) {
        try {
            contentResolver.openFileDescriptor(uri, "w")?.use { file ->
                FileOutputStream(file.fileDescriptor).use { stream ->
                    stream.write(exportData?.toByteArray())
                }
            }
        } catch (error: FileNotFoundException) {
            error.printStackTrace()
        } catch (error: IOException) {
            error.printStackTrace()
        }
    }
}