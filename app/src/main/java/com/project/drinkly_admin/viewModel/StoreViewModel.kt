package com.project.drinkly_admin.viewModel

import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.drinkly_admin.R
import com.project.drinkly_admin.api.ApiClient
import com.project.drinkly_admin.api.PresignedUrlApiClient
import com.project.drinkly_admin.api.TokenManager
import com.project.drinkly_admin.api.TokenUtil
import com.project.drinkly_admin.api.request.image.PresignedUrlBatchRequest
import com.project.drinkly_admin.api.request.image.PresignedUrlRequest
import com.project.drinkly_admin.api.request.image.PresignedUrlResponse
import com.project.drinkly_admin.api.request.image.StoreImageRequest
import com.project.drinkly_admin.api.request.store.StoreDetailRequest
import com.project.drinkly_admin.api.response.BaseResponse
import com.project.drinkly_admin.api.response.home.StoreDetailResponse
import com.project.drinkly_admin.ui.MainActivity
import com.project.drinkly_admin.ui.home.HomeFragment
import com.project.drinkly_admin.util.MyApplication
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class StoreViewModel : ViewModel() {

    var storeDetailInfo: MutableLiveData<StoreDetailResponse?> = MutableLiveData()
    var isEdit: MutableLiveData<Boolean> = MutableLiveData()

    var presignedUrl: MutableLiveData<PresignedUrlResponse> = MutableLiveData()
    var presignedUrlBatch: MutableLiveData<List<PresignedUrlResponse>> = MutableLiveData()

    fun getStoreDetail(activity: MainActivity, storeId: Int) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getStoreDetailInfo(storeId)
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

                        storeDetailInfo.value = result?.payload
                        MyApplication.storeId = result?.payload?.storeId ?: 0
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<StoreDetailResponse>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                        when(response.code()) {
                            498 -> {
                                TokenUtil.refreshToken(activity) {
                                    getStoreDetail(activity, storeId)
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<StoreDetailResponse>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())

                    activity.goToLogin()
                }
            })
    }

    fun editStoreInfo(activity: MainActivity, storeId: Int, storeInfo: StoreDetailRequest) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.editStoreInfo(tokenManager.getAccessToken().toString(), storeId, storeInfo)
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

                        MyApplication.storeId = result?.payload?.storeId ?: 0

                        if(storeInfo.isReady != null) {
                            activity.supportFragmentManager.beginTransaction()
                                .replace(R.id.fragmentContainerView_main, HomeFragment())
                                .commit()
                        } else {
                            storeDetailInfo.value = result?.payload

                            activity.supportFragmentManager.popBackStack()
                        }
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<StoreDetailResponse>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                        isEdit.value = false

                        when(response.code()) {
                            498 -> {
                                TokenUtil.refreshToken(activity) {
                                    editStoreInfo(activity, storeId, storeInfo)
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<StoreDetailResponse>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())
                    isEdit.value = false

                    activity.goToLogin()
                }
            })
    }

    fun editStoreImage(activity: MainActivity, storeId: Int, storeImageInfo: StoreImageRequest) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.editStoreImage(tokenManager.getAccessToken().toString(), storeId, storeImageInfo)
            .enqueue(object :
                Callback<BaseResponse<StoreDetailResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<StoreDetailResponse>>,
                    response: Response<BaseResponse<StoreDetailResponse>>
                ) {
                    Log.d("DrinklyViewModel", "onResponse 성공: --" + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<StoreDetailResponse>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 성공 - : " + result?.toString())

                        storeDetailInfo.value = result?.payload

                        activity.supportFragmentManager.popBackStack()
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<StoreDetailResponse>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                        isEdit.value = false

                        when(response.code()) {
                            498 -> {
                                TokenUtil.refreshToken(activity) {
                                    editStoreImage(activity, storeId, storeImageInfo)
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<StoreDetailResponse>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())
                    isEdit.value = false

                    activity.goToLogin()
                }
            })
    }

    fun getPresignedUrl(activity: MainActivity, image: File) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getPresignedUrl(tokenManager.getAccessToken().toString(), PresignedUrlRequest("${MyApplication.storeName}/main"))
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

                        savePresignedUrlImage(activity, result?.payload ?: PresignedUrlResponse("", ""), image) {}
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<PresignedUrlResponse>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                        when(response.code()) {
                            498 -> {
                                TokenUtil.refreshToken(activity) {
                                    getPresignedUrl(activity, image)
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<PresignedUrlResponse>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())

                    activity.goToLogin()
                }
            })
    }

    fun getPresignedUrlBatch(activity: MainActivity, images: List<Any>?, imageType : String) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        val request = images?.let {
            List(it.size) {
                PresignedUrlRequest(
                    prefix = "${MyApplication.storeName}/${imageType}",
                    fileName = "${imageType}"
                )
            }
        } ?: emptyList()

        apiClient.apiService.getPresignedUrlBatch(
            tokenManager.getAccessToken().toString(),
            PresignedUrlBatchRequest(request)
        ).enqueue(object : Callback<BaseResponse<List<PresignedUrlResponse>>> {

            override fun onResponse(
                call: Call<BaseResponse<List<PresignedUrlResponse>>>,
                response: Response<BaseResponse<List<PresignedUrlResponse>>>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()?.payload ?: emptyList()

                    if (images != null) {
                        if (result.size == images.size) {
                            var successCount = 0

                            result.zip(images).forEachIndexed { index, (urlData, imageFile) ->
                                savePresignedUrlImage(activity, urlData, imageFile as File) {
                                    successCount++

                                    // 모두 완료되면 LiveData에 할당
                                    if (successCount == images.size) {
                                        presignedUrlBatch.value = result
                                    }
                                }
                            }
                        } else {
                            Log.e("DrinklyViewModel", "URL 수와 이미지 수가 일치하지 않습니다.")
                        }
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("DrinklyViewModel", "Presigned 요청 실패: $errorBody")

                    when(response.code()) {
                        498 -> {
                            TokenUtil.refreshToken(activity) {
                                getPresignedUrlBatch(activity, images, imageType)
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<BaseResponse<List<PresignedUrlResponse>>>, t: Throwable) {
                Log.e("DrinklyViewModel", "Presigned 요청 실패: ${t.message}")

                activity.goToLogin()
            }
        })
    }


    fun savePresignedUrlImage(activity: MainActivity, presignedUrlData: PresignedUrlResponse, image: File, onSuccess: () -> Unit) {
        val apiClient = PresignedUrlApiClient(activity)
        val tokenManager = TokenManager(activity)

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
                        onSuccess()

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

                    activity.goToLogin()
                }
            })
    }
}