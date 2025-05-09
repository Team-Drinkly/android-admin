package com.project.drinkly_admin.api

import android.util.Log
import androidx.fragment.app.FragmentManager
import com.project.drinkly_admin.R
import com.project.drinkly_admin.api.response.BaseResponse
import com.project.drinkly_admin.api.response.login.ReissueTokenResponse
import com.project.drinkly_admin.ui.MainActivity
import com.project.drinkly_admin.ui.onboarding.LoginFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object TokenUtil {

    fun refreshToken(activity: MainActivity, retryRequest: () -> Unit) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.refreshToken("Bearer ${tokenManager.getRefreshToken()}")
            .enqueue(object : Callback<BaseResponse<ReissueTokenResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<ReissueTokenResponse>>,
                    response: Response<BaseResponse<ReissueTokenResponse>>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()

                        when(result?.result?.code) {
                            200 -> {
                                tokenManager.saveTokens(
                                    "Bearer ${result.payload?.accessToken.toString()}",
                                    result.payload?.refreshToken.toString()
                                )

                                retryRequest()
                            }

                            400 -> {
                                activity.goToLogin()
                            }
                        }
                    } else {
                        Log.e("TokenUtil", "재발급 실패: ${response.errorBody()?.string()}")

                        activity.goToLogin()
                    }
                }

                override fun onFailure(call: Call<BaseResponse<ReissueTokenResponse>>, t: Throwable) {
                    Log.e("TokenUtil", "onFailure: ${t.message}")

                    activity.goToLogin()
                }
            })
    }
}
