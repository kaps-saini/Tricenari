package com.mavalore.tricenari.domain.models.article

data class SingleArticleData(
    val blackquote: String,
    val contentBottom: String,
    val contentMiddle: String,
    val contentTop: String,
    val hashtagsArray: List<String>,
    val imgurl: String,
    val intTagsArray: List<String>
)