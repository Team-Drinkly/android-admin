package com.project.drinkly_admin.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.drinkly_admin.api.ApiClient
import com.project.drinkly_admin.api.TokenManager
import com.project.drinkly_admin.api.response.BaseResponse
import com.project.drinkly_admin.api.response.coupon.CouponListResponse
import com.project.drinkly_admin.api.response.home.FreeDrinkHistory
import com.project.drinkly_admin.ui.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CouponViewModel: ViewModel() {
    var storeCoupons: MutableLiveData<List<CouponListResponse>> = MutableLiveData()

    fun getCouponList(activity: MainActivity, storeId: Int) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getCouponList(tokenManager.getAccessToken().toString(), storeId)
            .enqueue(object :
                Callback<BaseResponse<List<CouponListResponse>>> {
                override fun onResponse(
                    call: Call<BaseResponse<List<CouponListResponse>>>,
                    response: Response<BaseResponse<List<CouponListResponse>>>
                ) {
                    Log.d("DrinklyViewModel", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<List<CouponListResponse>>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 성공: " + result?.toString())

                        storeCoupons.value = result?.payload!!
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<List<CouponListResponse>>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                    }
                }

                override fun onFailure(call: Call<BaseResponse<List<CouponListResponse>>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())
                }
            })
    }
}