package com.project.drinkly_admin.api.response

data class BaseResponse<T>(
    val result: BaseResult,
    val payload: T?
)

data class BaseResult(
    val code: Int,
    val message: String
)