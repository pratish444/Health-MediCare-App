package com.example.healthmedicareapp.data.local.dao

import androidx.room.*
import com.example.healthmedicareapp.data.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE userId = :userId")
    fun getUserById(userId: String): Flow

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Delete
    suspend fun deleteUser(user: UserEntity)
}