package com.mavalore.tricenari.domain.models.user

data class AuthUserResponse(
    val data: UserDetails,
    val status: Int,
    val status_message: String
)