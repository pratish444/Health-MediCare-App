package com.example.healthmedicareapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val userId: String,
    val email: String,
    val fullName: String,
    val mobileNumber: String,
    val lastSynced: Long = System.currentTimeMillis()
)