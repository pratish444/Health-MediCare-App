package com.example.healthmedicareapp.data.remote.firebase

import com.example.healthmedicareapp.domain.model.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreManager @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val usersCollection = firestore.collection("users")
    private val medicalDetailsCollection = firestore.collection("medical_details")

    suspend fun saveUser(user: User): Result<Unit> {
        return try {
            usersCollection.document(user.userId).set(
                hashMapOf(
                    "email" to user.email,
                    "fullName" to user.fullName,
                    "mobileNumber" to user.mobileNumber
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUser(userId: String): Result<User?> {
        return try {
            val document = usersCollection.document(userId).get().await()
            if (document.exists()) {
                Result.success(
                    User(
                        userId = document.id,
                        email = document.getString("email") ?: "",
                        fullName = document.getString("fullName") ?: "",
                        mobileNumber = document.getString("mobileNumber") ?: ""
                    )
                )
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveMedicalDetails(details: MedicalDetails): Result<Unit> {
        return try {
            medicalDetailsCollection.document(details.userId).set(
                hashMapOf(
                    "height" to details.height,
                    "weight" to details.weight,
                    "age" to details.age,
                    "bloodGroup" to details.bloodGroup,
                    "location" to details.location,
                    "gender" to details.gender,
                    "profession" to details.profession,
                    "systolicBP" to details.systolicBP,
                    "diastolicBP" to details.diastolicBP,
                    "bmi" to details.bmi,
                    "weightStatus" to details.weightStatus
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMedicalDetails(userId: String): Result<MedicalDetails?> {
        return try {
            val document = medicalDetailsCollection.document(userId).get().await()
            if (document.exists()) {
                Result.success(
                    MedicalDetails(
                        userId = document.id,
                        height = document.getLong("height")?.toInt() ?: 0,
                        weight = document.getLong("weight")?.toInt() ?: 0,
                        age = document.getLong("age")?.toInt() ?: 0,
                        bloodGroup = document.getString("bloodGroup") ?: "",
                        location = document.getString("location") ?: "",
                        gender = document.getString("gender") ?: "",
                        profession = document.getString("profession") ?: "",
                        systolicBP = document.getLong("systolicBP")?.toInt() ?: 0,
                        diastolicBP = document.getLong("diastolicBP")?.toInt() ?: 0,
                        bmi = document.getDouble("bmi")?.toFloat() ?: 0f,
                        weightStatus = document.getString("weightStatus") ?: ""
                    )
                )
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}