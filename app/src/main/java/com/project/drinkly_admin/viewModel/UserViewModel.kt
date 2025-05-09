package com.project.drinkly_admin.viewModel

import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.drinkly_admin.api.ApiClient
import com.project.drinkly_admin.api.TokenManager
import com.project.drinkly_admin.api.TokenUtil
import com.project.drinkly_admin.api.response.BaseResponse
import com.project.drinkly_admin.api.response.home.StoreDetailResponse
import com.project.drinkly_admin.api.response.home.StoreListResponse
import com.project.drinkly_admin.api.response.login.OwnerNameResponse
import com.project.drinkly_admin.ui.MainActivity
import com.project.drinkly_admin.util.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserViewModel : ViewModel() {

    var userName: MutableLiveData<String> = MutableLiveData()

    var storeList = MutableLiveData<MutableList<StoreListResponse>>()

    fun getOwnerName(activity: MainActivity) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getOwnerName(tokenManager.getUserId())
            .enqueue(object :
                Callback<BaseResponse<OwnerNameResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<OwnerNameResponse>>,
                    response: Response<BaseResponse<OwnerNameResponse>>
                ) {
                    Log.d("DrinklyViewModel", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<OwnerNameResponse>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 성공: " + result?.toString())

                        when(result?.result?.code) {
                            in 200 .. 299 -> {
                                userName.value = result?.payload?.ownerName
                            }

                            in 400..499 -> {
                                activity.goToLogin()
                            }
                        }
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<OwnerNameResponse>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                    }
                }

                override fun onFailure(call: Call<BaseResponse<OwnerNameResponse>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())

                    activity.goToLogin()
                }
            })
    }

    fun getStoreList(activity: MainActivity) {
        val tempStoreList = mutableListOf<StoreListResponse>()

        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getStoreList(tokenManager.getAccessToken().toString())
            .enqueue(object :
                Callback<BaseResponse<List<StoreListResponse>>> {
                override fun onResponse(
                    call: Call<BaseResponse<List<StoreListResponse>>>,
                    response: Response<BaseResponse<List<StoreListResponse>>>
                ) {
                    Log.d("DrinklyViewModel", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<List<StoreListResponse>>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 성공: " + result?.toString())

                        val resultStoreList = result?.payload ?: emptyList()

                        for (store in resultStoreList) {

                            val storeItem = StoreListResponse(
                                storeId = store.storeId,
                                storeName = store.storeName,
                                storeAddress = store.storeAddress
                            )

                            tempStoreList.add(storeItem)
                        }

                        storeList.value = tempStoreList
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<List<StoreListResponse>>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                        when(response.code()) {
                            498 -> {
                                TokenUtil.refreshToken(activity) {
                                    getStoreList(activity)
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<List<StoreListResponse>>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())

                    activity.goToLogin()
                }
            })
    }
}