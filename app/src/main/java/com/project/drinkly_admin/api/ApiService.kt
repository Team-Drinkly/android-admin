package com.project.drinkly_admin.api

import com.project.drinkly.api.response.login.LoginResponse
import com.project.drinkly_admin.api.response.BaseResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // OAuth로그인
    @POST("v1/member/oauth/OWNER/{provider}")
    fun login(
        @Path("provider") provider: String,
        @Header("Authorization") token: String
    ): Call<BaseResponse<LoginResponse>>

}