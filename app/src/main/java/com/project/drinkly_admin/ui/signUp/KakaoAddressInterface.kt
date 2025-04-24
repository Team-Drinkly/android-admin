package com.project.drinkly_admin.ui.signUp

import android.webkit.JavascriptInterface

class KakaoAddressInterface(
    private val onReceiveAddress: (String) -> Unit
) {
    @JavascriptInterface
    fun postMessage(data: String) {
        // data는 JSON 형태 문자열일 가능성 있음
        try {
            val json = org.json.JSONObject(data)
            val roadAddress = json.optString("roadAddress")
            if (roadAddress.isNotEmpty()) {
                onReceiveAddress(roadAddress)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
