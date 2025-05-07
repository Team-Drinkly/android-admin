package com.project.drinkly_admin.api.request.coupon

data class CouponNotificationRequest(
    val couponId: Int,
    val storeName: String
)