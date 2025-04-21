package com.project.drinkly_admin.api.openData

import com.project.drinkly_admin.api.openData.request.ValidateBusinessInfoRequest
import com.project.drinkly_admin.api.openData.response.ValidateBusinessInfoResponse
import com.project.drinkly_admin.api.request.login.BasicStoreInfoRequest
import com.project.drinkly_admin.api.response.BaseResponse
import com.project.drinkly_admin.api.response.login.BasicStoreInfoResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface DataApiService {
    // 사업자 정보 진위 확인
    @POST("validate")
    fun validateBusinessInfo(
        @Query("serviceKey") serviceKey: String,
        @Body request: ValidateBusinessInfoRequest
    ): Call<ValidateBusinessInfoResponse>
}