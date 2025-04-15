package com.project.drinkly_admin

import android.app.Application

class MyApplication : Application() {
    companion object {
        // 회원가입 정보
        var oauthId = 0
        var signUpPassAuthorization: Boolean? = null
    }
}