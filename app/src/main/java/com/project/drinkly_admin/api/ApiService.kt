package com.project.drinkly_admin.api

import com.project.drinkly.api.response.login.LoginResponse
import com.project.drinkly.api.response.login.NiceUrlResponse
import com.project.drinkly.api.response.login.SignUpResponse
import com.project.drinkly_admin.api.request.login.BasicStoreInfoRequest
import com.project.drinkly_admin.api.request.login.SignUpRequest
import com.project.drinkly_admin.api.response.BaseResponse
import com.project.drinkly_admin.api.response.home.StoreDetailResponse
import com.project.drinkly_admin.api.response.home.StoreListResponse
import com.project.drinkly_admin.api.response.login.BasicStoreInfoResponse
import com.project.drinkly_admin.api.response.login.OwnerNameResponse
import retrofit2.Call
import retrofit2.http.Body
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

    // NICE URL 데이터 받기
    @GET("v1/member/nice/owner/{memberId}")
    fun getNiceUrlData(
        @Path("memberId") memberId: Int
    ): Call<BaseResponse<NiceUrlResponse>>

    // NICE 데이터 전송
    @GET("v1/member/nice/call-back")
    fun callBackNiceData(
        @Query("id") id: String,
        @Query("type") type: String,
        @Query("token_version_id") token_version_id: String,
        @Query("enc_data") enc_data: String,
        @Query("integrity_value") integrity_value: String
    ): Call<BaseResponse<String>>

    // 회원가입
    @POST("v1/member/signup/owner")
    fun signUp(
        @Body request: SignUpRequest
    ): Call<BaseResponse<SignUpResponse>>

    // ID를 통한 이름 조회
    @GET("v1/member/validate/owner/{ownerId}")
    fun getOwnerNameForValid(
        @Path("ownerId") ownerId: Int
    ): Call<BaseResponse<OwnerNameResponse>>

    // ID를 통한 이름 조회 (회원가입 이후)
    @GET("v1/member/profile/owner/{ownerId}")
    fun getOwnerName(
        @Path("ownerId") ownerId: Int
    ): Call<BaseResponse<OwnerNameResponse>>

    // 업체 리스트 조회
    @GET("v1/store/o/owner")
    fun getStoreList(
        @Header("Authorization") token: String,
    ): Call<BaseResponse<List<StoreListResponse>>>

    // 업체 세부 정보 조회
    @GET("v1/store/m/list/{storeId}")
    fun getStoreDetailInfo(
        @Header("Authorization") token: String,
        @Path("storeId") storeId: Int
    ): Call<BaseResponse<StoreDetailResponse>>

    // 업체 기본 정보 저장
    @POST("v1/store/o")
    fun saveBasicStoreInfo(
        @Body request: BasicStoreInfoRequest
    ): Call<BaseResponse<BasicStoreInfoResponse>>
}