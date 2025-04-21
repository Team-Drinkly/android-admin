package com.project.drinkly_admin.api.response.login

data class BasicStoreInfoResponse(
    val storeId: Int,
    val ownerId: Int,
    val storeName: String,
    val storeMainImageUrl: String?,
    val storeDescription: String?,
    val isOpen: Boolean?,  // null 허용
    val openingInfo: String?,
    val openingHours: String?,
    val storeTel: String,
    val storeAddress: String,
    val storeDetailAddress: String,
    val instagramUrl: String?,
    val availableDays: String?,
    val latitude: String,
    val longitude: String,
    val availableDrinkImageUrls: List<String>?,
    val menuImageUrls: List<String>?
)