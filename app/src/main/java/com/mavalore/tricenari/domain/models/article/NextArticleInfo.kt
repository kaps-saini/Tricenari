package com.mavalore.tricenari.domain.models.article

data class NextArticleInfo(
    val data: ArticleData,
    val status: Int,
    val status_message: String
)