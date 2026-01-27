package com.example.healthmedicareapp.domain.repository

import com.example.healthmedicareapp.domain.model.MedicalDetails
import kotlinx.coroutines.flow.Flow

interface MedicalRepository {
    suspend fun saveMedicalDetails(details: MedicalDetails): Result<Unit>
    fun getMedicalDetails(userId: String): Flow<MedicalDetails>
}