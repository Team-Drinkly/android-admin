package com.project.drinkly.api.response.login

import com.project.drinkly_admin.api.response.login.BasicStoreInfoResponse

data class SignUpResponse(
    val token: TokenResponse,
    val registerStoreResponse: BasicStoreInfoResponse
)

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String
)