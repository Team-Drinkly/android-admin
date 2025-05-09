package com.project.drinkly_admin.api.openData.response

data class ValidateBusinessInfoResponse(
    val status_code: String,
    val match_cnt: Int?,
    val request_cnt: Int,
    val data: List<BizInfo>
)

data class BizInfo(
    val b_no: String,
    val b_stt: String,
    val b_stt_cd: String,
    val tax_type: String,
    val tax_type_cd: String,
    val end_dt: String,
    val utcc_yn: String,
    val tax_type_change_dt: String,
    val invoice_apply_dt: String,
    val rbf_tax_type: String,
    val rbf_tax_type_cd: String
)