package com.project.drinkly_admin.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.drinkly_admin.R
import com.project.drinkly_admin.api.ApiClient
import com.project.drinkly_admin.api.TokenManager
import com.project.drinkly_admin.api.TokenUtil
import com.project.drinkly_admin.api.request.coupon.CouponNotificationRequest
import com.project.drinkly_admin.api.request.coupon.CreateCouponRequest
import com.project.drinkly_admin.api.response.BaseResponse
import com.project.drinkly_admin.api.response.coupon.CouponListResponse
import com.project.drinkly_admin.api.response.home.FreeDrinkHistory
import com.project.drinkly_admin.ui.MainActivity
import com.project.drinkly_admin.ui.coupon.CouponCompleteFragment
import com.project.drinkly_admin.ui.coupon.CouponCreateFragment
import com.project.drinkly_admin.ui.signUp.SignUpAgreementFragment
import com.project.drinkly_admin.util.MyApplication
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

                        when(response.code()) {
                            498 -> {
                                TokenUtil.refreshToken(activity) {
                                    getCouponList(activity, storeId)
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<List<CouponListResponse>>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())
                }
            })
    }

    fun createCoupon(activity: MainActivity, couponTitle: String, couponDescription: String, couponNum: Int, couponDate: String) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        var couponInfo = CreateCouponRequest(MyApplication.storeId.toString(), MyApplication.storeName, couponTitle, couponDescription, couponNum, couponDate)

        apiClient.apiService.createCoupon(tokenManager.getAccessToken().toString(), couponInfo)
            .enqueue(object :
                Callback<BaseResponse<Int>> {
                override fun onResponse(
                    call: Call<BaseResponse<Int>>,
                    response: Response<BaseResponse<Int>>
                ) {
                    Log.d("DrinklyViewModel", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<Int>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 성공: " + result?.toString())

                        sendNotificationForCoupon(activity, result?.payload ?: 0, MyApplication.storeName)

                        activity.supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView_main, CouponCompleteFragment())
                            .commit()
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<Int>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                        when(response.code()) {
                            498 -> {
                                TokenUtil.refreshToken(activity) {
                                    createCoupon(activity, couponTitle, couponDescription, couponNum, couponDate)
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<Int>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())
                }
            })
    }

    fun sendNotificationForCoupon(activity: MainActivity, couponId: Int, storeName: String) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        var couponInfo = CouponNotificationRequest(couponId, storeName)

        apiClient.apiService.sendNotificationForCoupon(tokenManager.getAccessToken().toString(), couponInfo)
            .enqueue(object :
                Callback<BaseResponse<String>> {
                override fun onResponse(
                    call: Call<BaseResponse<String>>,
                    response: Response<BaseResponse<String>>
                ) {
                    Log.d("DrinklyViewModel", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<String>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 성공: " + result?.toString())

                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<String>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                        when(response.code()) {
                            498 -> {
                                TokenUtil.refreshToken(activity) {
                                    sendNotificationForCoupon(activity, couponId, storeName)
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<String>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())
                }
            })
    }
}