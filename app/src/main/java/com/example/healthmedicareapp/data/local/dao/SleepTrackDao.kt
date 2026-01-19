package com.example.healthmedicareapp.data.local.dao

import androidx.room.*
import com.example.healthmedicareapp.data.local.entities.SleepTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepTrackDao {
    @Insert
    suspend fun insertSleepTrack(track: SleepTrackEntity): Long

    @Update
    suspend fun updateSleepTrack(track: SleepTrackEntity)

    @Query("SELECT * FROM sleep_tracks WHERE userId = :userId ORDER BY startTime DESC")
    fun getAllSleepTracks(userId: String): Flow<List<SleepTrackEntity>>

    @Query("SELECT * FROM sleep_tracks WHERE id = :id")
    suspend fun getSleepTrackById(id: Long): SleepTrackEntity?

    @Query("SELECT * FROM sleep_tracks WHERE userId = :userId AND date = :date")
    suspend fun getSleepTrackByDate(userId: String, date: String): SleepTrackEntity?
}