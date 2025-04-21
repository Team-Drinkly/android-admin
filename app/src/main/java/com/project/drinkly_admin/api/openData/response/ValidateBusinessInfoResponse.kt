package com.project.drinkly_admin.api.openData.response

import com.google.gson.annotations.SerializedName

data class ValidateBusinessInfoResponse(
    @SerializedName("request_cnt")
    val requestCnt: Int,

    @SerializedName("valid_cnt")
    val validCnt: Int?,

    @SerializedName("status_code")
    val statusCode: String,

    val data: List<BusinessCheckItem>
)

data class BusinessCheckItem(
    @SerializedName("b_no")
    val bNo: String,

    val valid: String,

    @SerializedName("valid_msg")
    val validMsg: String?,

    @SerializedName("request_param")
    val requestParam: RequestParam,

    val status: BusinessStatus?
)

data class RequestParam(
    @SerializedName("b_no")
    val bNo: String,

    @SerializedName("start_dt")
    val startDt: String,

    @SerializedName("p_nm")
    val pNm: String
)

data class BusinessStatus(
    @SerializedName("b_no")
    val bNo: String,

    @SerializedName("b_stt")
    val bStt: String,

    @SerializedName("b_stt_cd")
    val bSttCd: String,

    @SerializedName("tax_type")
    val taxType: String,

    @SerializedName("tax_type_cd")
    val taxTypeCd: String,

    @SerializedName("end_dt")
    val endDt: String,

    @SerializedName("utcc_yn")
    val utccYn: String,

    @SerializedName("tax_type_change_dt")
    val taxTypeChangeDt: String,

    @SerializedName("invoice_apply_dt")
    val invoiceApplyDt: String,

    @SerializedName("rbf_tax_type")
    val rbfTaxType: String,

    @SerializedName("rbf_tax_type_cd")
    val rbfTaxTypeCd: String
)