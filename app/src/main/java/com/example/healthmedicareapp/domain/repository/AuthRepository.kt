package com.example.healthmedicareapp.domain.repository

import com.example.healthmedicareapp.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signUp(email: String, password: String, fullName: String, mobileNumber: String): Result
    suspend fun signIn(email: String, password: String): Result
    suspend fun sendPasswordResetEmail(email: String): Result
    suspend fun signOut()
    fun getCurrentUser(): Flow
    val isUserLoggedIn: Boolean
}