package com.example.healthmedicareapp.domain.repository

import com.example.healthmedicareapp.domain.model.SleepTrack
import kotlinx.coroutines.flow.Flow

interface SleepTrackRepository {
    suspend fun startSleepTracking(userId: String, startTime: Long, date: String): Result
    suspend fun stopSleepTracking(id: Long, endTime: Long): Result
    fun getAllSleepTracks(userId: String): Flow<List>
    suspend fun getSleepTrackByDate(userId: String, date: String): SleepTrack?
}