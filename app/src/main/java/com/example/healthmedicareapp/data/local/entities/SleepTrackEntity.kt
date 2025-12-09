package com.example.healthmedicareapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sleep_tracks")
data class SleepTrackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val startTime: Long,
    val endTime: Long?,
    val duration: Long,
    val date: String
)