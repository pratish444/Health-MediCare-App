package com.example.healthmedicareapp.domain.model

data class SleepTrack(
    val id: Long = 0,
    val userId: String,
    val startTime: Long,
    val endTime: Long?,
    val duration: Long,
    val date: String
)