package com.mavalore.tricenari.domain.models.event

data class EventDetailsResponse(
    val data: EventDetailsData,
    val status: Int,
    val status_message: String
)