package com.project.drinkly_admin.api.request.login

data class BasicStoreInfoRequest(
    var ownerId: Int,
    var storeName: String,
    var storeTel: String,
    var storeAddress: String,
    var storeDetailAddress: String?,
    var businessRegistrationNumber: String
)