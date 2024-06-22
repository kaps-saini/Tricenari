package com.mavalore.tricenari.domain.models.event

data class EventInfoResponse(
    val data: EventInfoData,
    val status: Int,
    val status_message: String
)