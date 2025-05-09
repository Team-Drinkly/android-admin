package com.project.drinkly_admin.viewModel

import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.drinkly.api.response.login.LoginResponse
import com.project.drinkly.api.response.login.NiceUrlResponse
import com.project.drinkly.api.response.login.SignUpResponse
import com.project.drinkly_admin.BuildConfig
import com.project.drinkly_admin.util.MyApplication
import com.project.drinkly_admin.R
import com.project.drinkly_admin.api.ApiClient
import com.project.drinkly_admin.api.TokenManager
import com.project.drinkly_admin.api.TokenUtil
import com.project.drinkly_admin.api.openData.DataApiClient
import com.project.drinkly_admin.api.openData.request.ValidateBusinessInfoRequest
import com.project.drinkly_admin.api.openData.response.ValidateBusinessInfoResponse
import com.project.drinkly_admin.api.request.login.SignUpRequest
import com.project.drinkly_admin.api.response.BaseResponse
import com.project.drinkly_admin.api.response.login.BasicStoreInfoResponse
import com.project.drinkly_admin.ui.MainActivity
import com.project.drinkly_admin.ui.home.HomeStoreListFragment
import com.project.drinkly_admin.ui.signUp.PassWebActivity
import com.project.drinkly_admin.ui.signUp.SignUpAgreementFragment
import com.project.drinkly_admin.ui.signUp.SignUpCompleteFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLEncoder

class LoginViewModel: ViewModel() {
    var passUrl: MutableLiveData<String> = MutableLiveData()

    var validBusinessInfo: MutableLiveData<Boolean> = MutableLiveData()

    fun validateBusinessInfo(activity: MainActivity, businessData: ValidateBusinessInfoRequest) {
        val apiClient = DataApiClient(activity)

        apiClient.apiService.validateBusinessInfo(BuildConfig.OPEN_DATA_KEY, businessData)
            .enqueue(object :
                Callback<ValidateBusinessInfoResponse> {
                override fun onResponse(
                    call: Call<ValidateBusinessInfoResponse>,
                    response: Response<ValidateBusinessInfoResponse>
                ) {
                    Log.d("DrinklyViewModel", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: ValidateBusinessInfoResponse? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 성공: " + result?.toString())

                        if(result?.match_cnt != null) {
                            validBusinessInfo.value = true
                        } else {
                            validBusinessInfo.value = false
                        }
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: ValidateBusinessInfoResponse? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                        validBusinessInfo.value = false
                    }
                }

                override fun onFailure(call: Call<ValidateBusinessInfoResponse>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())

                    validBusinessInfo.value = false
                }
            })
    }

    fun login(activity: MainActivity, provider: String, token: String) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.login(provider,token)
            .enqueue(object :
                Callback<BaseResponse<LoginResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<LoginResponse>>,
                    response: Response<BaseResponse<LoginResponse>>
                ) {
                    Log.d("DrinklyViewModel", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<LoginResponse>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 성공: " + result?.toString())

                        if(result?.payload?.isRegistered == true) {
                            MyApplication.oauthId = result.payload.oauthId ?: 0
                            tokenManager.saveUserId(result.payload.oauthId ?: 0)
                            tokenManager.saveTokens("Bearer ${result.payload.accessToken}", result.payload.refreshToken)

                            // 매장 리스트 화면으로 이동
                            activity.supportFragmentManager.beginTransaction()
                                .replace(R.id.fragmentContainerView_main, HomeStoreListFragment())
                                .commit()
                        } else {
                            MyApplication.oauthId = result?.payload?.oauthId ?: 0
                            tokenManager.saveUserId(result?.payload?.oauthId ?: 0)
                            // 회원가입 화면으로 이동
                            activity.supportFragmentManager.beginTransaction()
                                .replace(R.id.fragmentContainerView_main, SignUpAgreementFragment())
                                .addToBackStack(null)
                                .commit()
                        }
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<LoginResponse>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                    }
                }

                override fun onFailure(call: Call<BaseResponse<LoginResponse>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())

                    activity.goToLogin()
                }
            })
    }

    fun getNiceUrlData(activity: PassWebActivity, memberId: Int) {
        val apiClient = ApiClient(activity)

        apiClient.apiService.getNiceUrlData(memberId)
            .enqueue(object :
                Callback<BaseResponse<NiceUrlResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<NiceUrlResponse>>,
                    response: Response<BaseResponse<NiceUrlResponse>>
                ) {
                    Log.d("DrinklyViewModel", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<NiceUrlResponse>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 성공: " + result?.toString())

                        var encode_enc = URLEncoder.encode(result?.payload?.enc_data ?: "", "UTF-8")
                        var encode_integrity = URLEncoder.encode(result?.payload?.integrity_value ?: "", "UTF-8")
                        var URL_INFO = BuildConfig.PASS_URL + "?m=service&token_version_id=${result?.payload?.token_version_id}&enc_data=${encode_enc}&integrity_value=${encode_integrity}"

                        passUrl.value = URL_INFO

                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<NiceUrlResponse>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                    }
                }

                override fun onFailure(call: Call<BaseResponse<NiceUrlResponse>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())
                }
            })
    }

    fun callBackNiceData(activity: PassWebActivity, memberId: String?, tokenVersionId: String?, encData: String?, integrityValue: String?) {
        val apiClient = ApiClient(activity)

        apiClient.apiService.callBackNiceData(memberId!!, "owner", tokenVersionId!!, encData!!, integrityValue!!)
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

                        activity.finish()

                        if(response.body()?.result?.code == 403) {
                            MyApplication.signUpPassAuthorization = false
                        } else {
                            MyApplication.signUpPassAuthorization = true
                        }
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<String>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                    }
                }

                override fun onFailure(call: Call<BaseResponse<String>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())
                }
            })
    }

    fun signUp(activity: MainActivity) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.signUp(SignUpRequest(tokenManager.getUserId(), MyApplication.basicStoreInfo))
            .enqueue(object :
                Callback<BaseResponse<SignUpResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<SignUpResponse>>,
                    response: Response<BaseResponse<SignUpResponse>>
                ) {
                    Log.d("DrinklyViewModel", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<SignUpResponse>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 성공: " + result?.toString())

                        tokenManager.saveTokens("Bearer ${result?.payload?.token?.accessToken}", result?.payload?.token?.refreshToken.toString())
                        tokenManager.saveUserId(result?.payload?.registerStoreResponse?.ownerId ?: 0)

                        activity.supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView_main, SignUpCompleteFragment())
                            .addToBackStack(null)
                            .commit()
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<SignUpResponse>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                    }
                }

                override fun onFailure(call: Call<BaseResponse<SignUpResponse>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())

                    activity.goToLogin()
                }
            })
    }

    fun saveBasicStoreInfo(activity: MainActivity) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.saveBasicStoreInfo(MyApplication.basicStoreInfo)
            .enqueue(object :
                Callback<BaseResponse<BasicStoreInfoResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<BasicStoreInfoResponse>>,
                    response: Response<BaseResponse<BasicStoreInfoResponse>>
                ) {
                    Log.d("DrinklyViewModel", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<BasicStoreInfoResponse>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 성공: " + result?.toString())

                        activity.supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView_main, SignUpCompleteFragment())
                            .addToBackStack(null)
                            .commit()

                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<BasicStoreInfoResponse>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                        when(response.code()) {
                            498 -> {
                                TokenUtil.refreshToken(activity) {
                                    saveBasicStoreInfo(activity)
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<BasicStoreInfoResponse>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())

                    activity.goToLogin()
                }
            })
    }
}