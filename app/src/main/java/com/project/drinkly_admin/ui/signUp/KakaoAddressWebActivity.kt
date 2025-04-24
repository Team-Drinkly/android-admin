package com.project.drinkly_admin.ui.signUp

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.project.drinkly_admin.R
import com.project.drinkly_admin.databinding.ActivityKakaoAddressWebBinding

class KakaoAddressWebActivity : AppCompatActivity() {

    lateinit var binding: ActivityKakaoAddressWebBinding
    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityKakaoAddressWebBinding.inflate(layoutInflater)
        setContentView(binding.root)

        webView = binding.webViewKakaoAddress

        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()
        webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW


//        webView.addJavascriptInterface(
//            WebAppInterface { address ->
//                // 콜백 주소 처리
//                runOnUiThread {
//                    // 예시: 주소를 Intent로 넘기고 종료
//                    val resultIntent = intent
//                    resultIntent.putExtra("address", address)
//                    setResult(RESULT_OK, resultIntent)
//                    finish()
//                }
//            }, "callbackHandler"
//        )

        webView.addJavascriptInterface(
            KakaoAddressInterface { address ->
                runOnUiThread {
                    // 받은 도로명 주소 처리
                    val resultIntent = intent
                    resultIntent.putExtra("address", address)
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }
            }, "callbackHandler"
        )


        webView.loadUrl("https://boogios.github.io/KakaoAddress/")
    }

    class WebAppInterface(private val callback: (String) -> Unit) {
        @JavascriptInterface
        fun postMessage(data: String) {
            callback(data)
        }
    }
}
