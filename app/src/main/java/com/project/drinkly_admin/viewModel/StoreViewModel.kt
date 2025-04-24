package com.project.drinkly_admin.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.drinkly_admin.BuildConfig
import com.project.drinkly_admin.api.ApiClient
import com.project.drinkly_admin.api.PresignedUrlApiClient
import com.project.drinkly_admin.api.TokenManager
import com.project.drinkly_admin.api.request.image.PresignedUrlRequest
import com.project.drinkly_admin.api.request.image.PresignedUrlResponse
import com.project.drinkly_admin.api.response.BaseResponse
import com.project.drinkly_admin.api.response.home.StoreDetailResponse
import com.project.drinkly_admin.ui.MainActivity
import com.project.drinkly_admin.util.MyApplication
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.net.URLDecoder

class StoreViewModel : ViewModel() {

    var storeDetailInfo: MutableLiveData<StoreDetailResponse> = MutableLiveData()
    var isEdit: MutableLiveData<Boolean> = MutableLiveData()

    var presignedUrl: MutableLiveData<PresignedUrlResponse> = MutableLiveData()

    fun getStoreDetail(activity: MainActivity, storeId: Int) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getStoreDetailInfo(tokenManager.getAccessToken().toString(), storeId)
            .enqueue(object :
                Callback<BaseResponse<StoreDetailResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<StoreDetailResponse>>,
                    response: Response<BaseResponse<StoreDetailResponse>>
                ) {
                    Log.d("DrinklyViewModel", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<StoreDetailResponse>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 성공: " + result?.toString())

                        storeDetailInfo.value = result?.payload!!
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<StoreDetailResponse>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                    }
                }

                override fun onFailure(call: Call<BaseResponse<StoreDetailResponse>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())
                }
            })
    }
    fun getPresignedUrl(activity: MainActivity, image: File) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getPresignedUrl(tokenManager.getAccessToken().toString(), PresignedUrlRequest(MyApplication.storeName))
            .enqueue(object :
                Callback<BaseResponse<PresignedUrlResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<PresignedUrlResponse>>,
                    response: Response<BaseResponse<PresignedUrlResponse>>
                ) {
                    Log.d("DrinklyViewModel", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<PresignedUrlResponse>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 성공: " + result?.toString())

                        savePresignedUrlImage(activity, result?.payload ?: PresignedUrlResponse("", ""), image)
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<PresignedUrlResponse>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                    }
                }

                override fun onFailure(call: Call<BaseResponse<PresignedUrlResponse>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())
                }
            })
    }

    fun savePresignedUrlImage(activity: MainActivity, presignedUrlData: PresignedUrlResponse, image: File) {
        val apiClient = PresignedUrlApiClient(activity)

        val requestBody = image.asRequestBody("image/jpeg".toMediaType())
        val call = apiClient.apiService.uploadFileToS3(presignedUrlData.url, requestBody)

        call.enqueue(object :
                Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    Log.d("DrinklyViewModel", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: ResponseBody? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 성공: " + result?.toString())

                        presignedUrl.value = presignedUrlData
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: ResponseBody? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())
                }
            })
    }
}