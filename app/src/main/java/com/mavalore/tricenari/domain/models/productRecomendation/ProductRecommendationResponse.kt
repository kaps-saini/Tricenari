package com.mavalore.tricenari.domain.models.productRecomendation

data class ProductRecommendationResponse(
    val data: List<RecommendedItemsData>,
    val status: Int,
    val status_message: String
)