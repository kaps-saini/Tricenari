package com.mavalore.tricenari.domain.models

import java.io.Serializable

data class SuperWomenData(
    val active: String? = null,
    val created_on: String,
    val id: Int,
    val name: String,
    val popular: String? = null,
    val remarks: Any? = null,
    val role: String
):Serializable