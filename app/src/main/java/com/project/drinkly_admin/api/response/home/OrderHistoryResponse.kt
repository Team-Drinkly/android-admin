package com.project.drinkly_admin.api.response.home

data class OrderHistoryResponse(
    val storeName: String,
    val getFreeDrinkHistoryResponseList: List<FreeDrinkHistory>
)

data class FreeDrinkHistory(
    val freeDrinkHistoryId: Int,
    val providedDrink: String,
    val memberId: Int,
    val memberNickname: String,
    val createdAt: String
)