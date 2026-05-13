package com.example.healthmedicareapp.data.repository

import com.example.healthmedicareapp.data.local.dao.SleepTrackDao
import com.example.healthmedicareapp.data.local.entities.SleepTrackEntity
import com.example.healthmedicareapp.domain.model.SleepTrack
import com.example.healthmedicareapp.domain.repository.SleepTrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SleepTrackRepositoryImpl @Inject constructor(
    private val sleepTrackDao: SleepTrackDao
) : SleepTrackRepository {

    override suspend fun startSleepTracking(userId: String, startTime: Long, date: String): Result<Unit> {
        return try {
            sleepTrackDao.insertSleepTrack(
                SleepTrackEntity(
                    userId = userId,
                    startTime = startTime,
                    endTime = null,
                    duration = 0,
                    date = date
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun stopSleepTracking(id: Long, endTime: Long): Result<Unit> {
        return try {
            val track = sleepTrackDao.getSleepTrackById(id)
            if (track != null) {
                val duration = endTime - track.startTime
                sleepTrackDao.updateSleepTrack(
                    track.copy(endTime = endTime, duration = duration)
                )
                Result.success(Unit)
            } else {
                Result.failure(Exception("Sleep track not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getAllSleepTracks(userId: String): Flow<List<SleepTrack>> {
        return sleepTrackDao.getAllSleepTracks(userId).map { entities ->
            entities.map { entity ->
                SleepTrack(
                    id = entity.id,
                    userId = entity.userId,
                    startTime = entity.startTime,
                    endTime = entity.endTime,
                    duration = entity.duration,
                    date = entity.date
                )
            }
        }
    }

    override suspend fun getSleepTrackByDate(userId: String, date: String): SleepTrack? {
        val entity = sleepTrackDao.getSleepTrackByDate(userId, date) ?: return null
        return SleepTrack(
            id = entity.id,
            userId = entity.userId,
            startTime = entity.startTime,
            endTime = entity.endTime,
            duration = entity.duration,
            date = entity.date
        )
    }
}