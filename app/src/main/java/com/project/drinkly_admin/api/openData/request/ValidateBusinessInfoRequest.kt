package com.project.drinkly_admin.api.openData.request

import com.google.gson.annotations.SerializedName

data class ValidateBusinessInfoRequest(
    val businesses: List<BusinessCheck>
)

data class BusinessCheck(
    @SerializedName("b_no")
    val bNo: String,

    @SerializedName("start_dt")
    val startDt: String,

    @SerializedName("p_nm")
    val pNm: String
)