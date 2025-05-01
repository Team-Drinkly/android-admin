package com.project.drinkly_admin.api.request.image

data class PresignedUrlResponse(
    var url: String,
    var filePath: String
)
