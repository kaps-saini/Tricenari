package com.mavalore.tricenari.domain.models.superwomen

data class SuperWomenResponse(
    val data: List<SuperWomenData>,
    val status: Int,
    val status_message: String
)