package com.mavalore.tricenari.domain.models.user

data class UserDetails(
    val IDverified: Int,
    val city: String?,
    val dob: String?,
    val email: String,
    val gender: String?,
    val id: Int,
    val interests: String?,
    val jewels: Int?,
    val loggedin: Boolean,
    val mobile: String?,
    val name: String,
    val otpVerified: Int,
    val proceed: Int,
    val provider: String
)