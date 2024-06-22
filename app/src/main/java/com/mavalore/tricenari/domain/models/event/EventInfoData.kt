package com.mavalore.tricenari.domain.models.event

data class EventInfoData(
    val duration: Int,
    val endtime: String,
    val evdate: String,
    val fee: Int,
    val guest: String,
    val id: Int,
    val lastdate: String,
    val maxseats: Int,
    val mode: String,
    val remainingseats: Int,
    val starttime: String,
    val subtitle: String,
    val tags: Any,
    val title: String,
    val venue: String
)