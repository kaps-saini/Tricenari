package com.mavalore.tricenari.domain.models.article

data class AllArticleResponse(
    val data: List<ArticleData>,
    val status: Int,
    val status_message: String
)