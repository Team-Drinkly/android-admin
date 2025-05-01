package com.project.drinkly_admin.api

import com.project.drinkly.api.response.login.LoginResponse
import com.project.drinkly.api.response.login.NiceUrlResponse
import com.project.drinkly.api.response.login.SignUpResponse
import com.project.drinkly_admin.api.request.image.PresignedUrlBatchRequest
import com.project.drinkly_admin.api.request.image.PresignedUrlRequest
import com.project.drinkly_admin.api.request.image.PresignedUrlResponse
import com.project.drinkly_admin.api.request.image.StoreImageRequest
import com.project.drinkly_admin.api.request.login.BasicStoreInfoRequest
import com.project.drinkly_admin.api.request.login.SignUpRequest
import com.project.drinkly_admin.api.request.store.StoreDetailRequest
import com.project.drinkly_admin.api.response.BaseResponse
import com.project.drinkly_admin.api.response.home.FreeDrinkHistory
import com.project.drinkly_admin.api.response.home.OrderHistoryResponse
import com.project.drinkly_admin.api.response.home.StoreDetailResponse
import com.project.drinkly_admin.api.response.home.StoreListResponse
import com.project.drinkly_admin.api.response.login.BasicStoreInfoResponse
import com.project.drinkly_admin.api.response.login.OwnerNameResponse
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url
import java.io.File

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
    @POST("v1/member/signup/owner/store")
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
        @Path("storeId") storeId: Int
    ): Call<BaseResponse<StoreDetailResponse>>

    // 매장 정보 수정
    @PATCH("v1/store/o/{storeId}")
    fun editStoreInfo(
        @Header("Authorization") token: String,
        @Path("storeId") storeId: Int,
        @Body request: StoreDetailRequest
    ): Call<BaseResponse<StoreDetailResponse>>

    // 매장 정보 수정 (메뉴판, 이용 가능한 주류)
    @PATCH("v1/store/o/{storeId}/images")
    fun editStoreImage(
        @Header("Authorization") token: String,
        @Path("storeId") storeId: Int,
        @Body request: StoreImageRequest
    ): Call<BaseResponse<StoreDetailResponse>>

    // presigned-url 생성
    @POST("v1/store/o/presigned-url")
    fun getPresignedUrl(
        @Header("Authorization") token: String,
        @Body request: PresignedUrlRequest
    ): Call<BaseResponse<PresignedUrlResponse>>

    // presigned-url 배치 생성
    @POST("v1/store/o/presigned-url/batch")
    fun getPresignedUrlBatch(
        @Header("Authorization") token: String,
        @Body request: PresignedUrlBatchRequest
    ): Call<BaseResponse<List<PresignedUrlResponse>>>

    // presigned-url 이미지 업로드
    @PUT
    fun uploadFileToS3(
        @Url url: String,
        @Body image: RequestBody
    ): Call<ResponseBody>

    // 업체 기본 정보 저장
    @POST("v1/store/o")
    fun saveBasicStoreInfo(
        @Body request: BasicStoreInfoRequest
    ): Call<BaseResponse<BasicStoreInfoResponse>>

    // 업체 기본 정보 저장 (3개)
    @GET("v1/store/o/{storeId}")
    fun getHomeOrderHistory(
        @Header("Authorization") token: String,
        @Path("storeId") storeId: Int
    ): Call<BaseResponse<OrderHistoryResponse>>

    // 업체 기본 정보 저장
    @GET("v1/store/o/free-drink/{storeId}")
    fun getOrderHistory(
        @Header("Authorization") token: String,
        @Path("storeId") storeId: Int
    ): Call<BaseResponse<List<FreeDrinkHistory>>>
}