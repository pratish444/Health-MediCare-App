package com.example.healthmedicareapp.data.repository

import com.example.healthmedicareapp.data.local.dao.MedicalDetailsDao
import com.example.healthmedicareapp.data.local.entities.MedicalDetailsEntity
import com.example.healthmedicareapp.data.remote.firebase.FirestoreManager
import com.example.healthmedicareapp.domain.model.MedicalDetails
import com.example.healthmedicareapp.domain.repository.MedicalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MedicalRepositoryImpl @Inject constructor(
    private val medicalDetailsDao: MedicalDetailsDao,
    private val firestoreManager: FirestoreManager
) : MedicalRepository {

    override suspend fun saveMedicalDetails(details: MedicalDetails): Result {
        return try {
            // Save to Firestore
            val firestoreResult = firestoreManager.saveMedicalDetails(details)

            // Cache locally
            medicalDetailsDao.insertMedicalDetails(
                MedicalDetailsEntity(
                    userId = details.userId,
                    height = details.height,
                    weight = details.weight,
                    age = details.age,
                    bloodGroup = details.bloodGroup,
                    location = details.location,
                    gender = details.gender,
                    profession = details.profession,
                    systolicBP = details.systolicBP,
                    diastolicBP = details.diastolicBP,
                    bmi = details.bmi,
                    weightStatus = details.weightStatus
                )
            )

            firestoreResult
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getMedicalDetails(userId: String): Flow {
        return medicalDetailsDao.getMedicalDetails(userId).map { entity ->
            entity?.let {
                MedicalDetails(
                    userId = it.userId,
                    height = it.height,
                    weight = it.weight,
                    age = it.age,
                    bloodGroup = it.bloodGroup,
                    location = it.location,
                    gender = it.gender,
                    profession = it.profession,
                    systolicBP = it.systolicBP,
                    diastolicBP = it.diastolicBP,
                    bmi = it.bmi,
                    weightStatus = it.weightStatus
                )
            }
        }
    }
}