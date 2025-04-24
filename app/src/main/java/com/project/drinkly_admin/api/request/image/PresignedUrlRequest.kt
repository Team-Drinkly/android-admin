package com.project.drinkly_admin.api.request.image

data class PresignedUrlRequest(
    var prefix: String,
    var fileName: String = "mainImage"
)
