package com.project.drinkly_admin.api.response.coupon

data class CouponListResponse(
    val id: Int,
    val title: String,
    val description: String,
    val expirationDate: String,
    val remainingCount: Int,
    val totalCount: Int,
    val available: Boolean
)