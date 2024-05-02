package com.mavalore.tricenari.domain.models.user

data class AddUserResponse(
    val data: UserIdAndOtp,
    val status: Int,
    val status_message: String
)