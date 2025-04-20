package com.project.drinkly_admin.util

import android.app.Application
import com.project.drinkly_admin.api.request.login.BasicStoreInfoRequest

class MyApplication : Application() {
    companion object {
        // 회원가입 정보
        var oauthId = 0
        var signUpPassAuthorization: Boolean? = null
    }
}