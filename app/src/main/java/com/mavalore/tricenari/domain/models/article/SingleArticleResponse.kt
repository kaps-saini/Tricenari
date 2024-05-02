package com.mavalore.tricenari.domain.models.article

data class SingleArticleResponse(
    val data: SingleArticleData,
    val status: Int,
    val status_message: String
)