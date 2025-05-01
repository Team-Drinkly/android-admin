package com.project.drinkly_admin.api.request.image

data class PresignedUrlBatchRequest(
    var requests: List<PresignedUrlRequest>
)
