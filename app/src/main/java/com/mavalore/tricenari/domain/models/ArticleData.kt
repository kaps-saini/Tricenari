package com.mavalore.tricenari.domain.models

import java.io.Serializable

data class ArticleData(
    val categ: Int? = null,
    val id: Int? = null,
    val longTitle: String? = null,
    val published: Any? = null,
    val shortTitle: String? = null
):Serializable