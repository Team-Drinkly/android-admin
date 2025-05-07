package com.project.drinkly_admin.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.drinkly_admin.api.ApiClient
import com.project.drinkly_admin.api.TokenManager
import com.project.drinkly_admin.api.TokenUtil
import com.project.drinkly_admin.api.response.BaseResponse
import com.project.drinkly_admin.api.response.home.FreeDrinkHistory
import com.project.drinkly_admin.api.response.home.OrderHistoryResponse
import com.project.drinkly_admin.ui.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderViewModel: ViewModel() {

    var storeHomeOrderHistory: MutableLiveData<OrderHistoryResponse> = MutableLiveData()
    var storeOrderHistory: MutableLiveData<List<FreeDrinkHistory>> = MutableLiveData()

    fun getHomeOrderHistory(activity: MainActivity, storeId: Int) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getHomeOrderHistory(tokenManager.getAccessToken().toString(), storeId)
            .enqueue(object :
                Callback<BaseResponse<OrderHistoryResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<OrderHistoryResponse>>,
                    response: Response<BaseResponse<OrderHistoryResponse>>
                ) {
                    Log.d("DrinklyViewModel", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<OrderHistoryResponse>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 성공: " + result?.toString())

                        storeHomeOrderHistory.value = result?.payload!!
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<OrderHistoryResponse>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                        when(response.code()) {
                            498 -> {
                                TokenUtil.refreshToken(activity) {
                                    getHomeOrderHistory(activity, storeId)
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<OrderHistoryResponse>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())
                }
            })
    }

    fun getOrderHistory(activity: MainActivity, storeId: Int) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getOrderHistory(tokenManager.getAccessToken().toString(), storeId)
            .enqueue(object :
                Callback<BaseResponse<List<FreeDrinkHistory>>> {
                override fun onResponse(
                    call: Call<BaseResponse<List<FreeDrinkHistory>>>,
                    response: Response<BaseResponse<List<FreeDrinkHistory>>>
                ) {
                    Log.d("DrinklyViewModel", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<List<FreeDrinkHistory>>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 성공: " + result?.toString())

                        storeOrderHistory.value = result?.payload!!
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<List<FreeDrinkHistory>>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                        when(response.code()) {
                            498 -> {
                                TokenUtil.refreshToken(activity) {
                                    getOrderHistory(activity, storeId)
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<List<FreeDrinkHistory>>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())
                }
            })
    }
}