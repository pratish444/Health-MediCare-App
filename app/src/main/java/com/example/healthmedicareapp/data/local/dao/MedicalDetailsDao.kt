package com.example.healthmedicareapp.data.local.dao

import androidx.room.*
import com.example.healthmedicareapp.data.local.entities.MedicalDetailsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicalDetailsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicalDetails(details: MedicalDetailsEntity)

    @Query("SELECT * FROM medical_details WHERE userId = :userId")
    fun getMedicalDetails(userId: String): Flow<MedicalDetailsEntity?>

    @Update
    suspend fun updateMedicalDetails(details: MedicalDetailsEntity)
}