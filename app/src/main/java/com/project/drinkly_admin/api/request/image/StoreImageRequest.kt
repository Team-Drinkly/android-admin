package com.project.drinkly_admin.api.request.image

data class StoreImageRequest(
    val type: String,
    val newImageUrls: List<NewImageUrl>,
    val removeImageIds: List<Int>
)

data class NewImageUrl(
    val imageUrl: String,
    val description: String
)

data class ImageData(
    val image: Any,
    val description: String
)

data class CommonImageData(
    val image: Any,
    val type: String,
    val description: String
)