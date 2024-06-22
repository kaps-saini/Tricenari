package com.mavalore.tricenari.domain.models.event

data class EventDetailsData(
    val details: List<String>,
    val faq: List<Faq>,
    val guest: Guest,
    val whocan: List<String>
)