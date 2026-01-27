package com.example.healthmedicareapp.domain.repository

import com.example.healthmedicareapp.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signUp(email: String, password: String, fullName: String, mobileNumber: String): Result<User>
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    suspend fun signOut()
    fun getCurrentUser(): Flow<User?>
    val isUserLoggedIn: Boolean
}