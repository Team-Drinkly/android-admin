package com.project.drinkly_admin.api.request.store

data class StoreDetailRequest(
    val storeName: String? = null,
    val storeMainImageUrl: String? = null,
    val storeDescription: String? = null,
    val openingHours: List<OpeningHour>? = null,
    val storeTel: String? = null,
    val storeAddress: String? = null,
    val storeDetailAddress: String? = null,
    val instagramUrl: String? = null,
    val availableDays: String? = null,
    val latitude: String? = null,
    val longitude: String? = null
)


data class OpeningHour(
    val day: String? = null,
    val isOpen: Boolean? = null,
    val openTime: String? = null,
    val closeTime: String? = null
)