package com.example.healthmedicareapp.domain.repository

import com.example.healthmedicareapp.domain.model.SleepTrack
import kotlinx.coroutines.flow.Flow

interface SleepTrackRepository {
    suspend fun startSleepTracking(userId: String, startTime: Long, date: String): Result<Unit>
    suspend fun stopSleepTracking(id: Long, endTime: Long): Result<Unit>
    fun getAllSleepTracks(userId: String): Flow<List<SleepTrack>>
    suspend fun getSleepTrackByDate(userId: String, date: String): SleepTrack?
}