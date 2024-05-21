package com.mavalore.tricenari.domain.models.dynamicValues

data class DynamicValuesResponse(
    val data: DynamicValues,
    val status: Int,
    val status_message: String
)