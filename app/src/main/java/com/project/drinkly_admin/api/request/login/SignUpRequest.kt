package com.project.drinkly_admin.api.request.login

data class SignUpRequest(
    val ownerId: Int,
    val registerStoreRequest: BasicStoreInfoRequest
)
