package com.project.drinkly_admin.api.response.login

data class ReissueTokenResponse(
    val accessToken: String,
    val refreshToken: String
)
