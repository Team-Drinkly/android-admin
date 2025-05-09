package com.project.drinkly_admin.api.openData.request

import com.google.gson.annotations.SerializedName

data class ValidateBusinessInfoRequest(
    @SerializedName("b_no")
    val bNo: List<String>
)