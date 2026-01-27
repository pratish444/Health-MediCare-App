package com.example.healthmedicareapp.data.repository

import com.example.healthmedicareapp.data.local.dao.UserDao
import com.example.healthmedicareapp.data.local.entities.UserEntity
import com.example.healthmedicareapp.data.remote.firebase.FirebaseAuthManager
import com.example.healthmedicareapp.data.remote.firebase.FirestoreManager
import com.example.healthmedicareapp.domain.model.User
import com.example.healthmedicareapp.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject



class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuthManager: FirebaseAuthManager,
    private val firestoreManager: FirestoreManager,
    private val userDao: UserDao
) : AuthRepository {

    override suspend fun signUp(
        email: String,
        password: String,
        fullName: String,
        mobileNumber: String
    ): Result<User> {
        return try {
            val authResult = firebaseAuthManager.signUp(email, password)
            if (authResult.isSuccess) {
                val firebaseUser = authResult.getOrNull()!!
                val user = User(
                    userId = firebaseUser.uid,
                    email = email,
                    fullName = fullName,
                    mobileNumber = mobileNumber
                )

                // Save to Firestore
                firestoreManager.saveUser(user)

                // Cache locally
                userDao.insertUser(
                    UserEntity(
                        userId = user.userId,
                        email = user.email,
                        fullName = user.fullName,
                        mobileNumber = user.mobileNumber
                    )
                )

                Result.success(user)
            } else {
                Result.failure(authResult.exceptionOrNull()!!)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val authResult = firebaseAuthManager.signIn(email, password)
            if (authResult.isSuccess) {
                val firebaseUser = authResult.getOrNull()!!
                val firestoreResult = firestoreManager.getUser(firebaseUser.uid)

                if (firestoreResult.isSuccess) {
                    val user = firestoreResult.getOrNull()!!
                    // Cache locally
                    userDao.insertUser(
                        UserEntity(
                            userId = user.userId,
                            email = user.email,
                            fullName = user.fullName,
                            mobileNumber = user.mobileNumber
                        )
                    )
                    Result.success(user)
                } else {
                    Result.failure(firestoreResult.exceptionOrNull()!!)
                }
            } else {
                Result.failure(authResult.exceptionOrNull()!!)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return firebaseAuthManager.sendPasswordResetEmail(email)
    }

    override suspend fun signOut() {
        firebaseAuthManager.signOut()
    }

    override fun getCurrentUser(): Flow<User?> {
        val currentUser = firebaseAuthManager.currentUser
        return if (currentUser != null) {
            userDao.getUserById(currentUser.uid).map { entity ->
                entity?.let {
                    User(
                        userId = it.userId,
                        email = it.email,
                        fullName = it.fullName,
                        mobileNumber = it.mobileNumber
                    )
                }
            }
        } else {
            kotlinx.coroutines.flow.flowOf(null)
        }
    }

    override val isUserLoggedIn: Boolean
        get() = firebaseAuthManager.currentUser != null
}