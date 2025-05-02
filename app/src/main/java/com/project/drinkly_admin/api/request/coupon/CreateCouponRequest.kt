package com.project.drinkly_admin.api.request.coupon

data class CreateCouponRequest(
    val storeId: String,
    val storeName: String,
    val title: String,
    val description: String,
    val count: Int,
    val expirationDate: String
)