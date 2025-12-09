package com.example.healthmedicareapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medical_details")
data class MedicalDetailsEntity(
    @PrimaryKey
    val userId: String,
    val height: Int,
    val weight: Int,
    val age: Int,
    val bloodGroup: String,
    val location: String,
    val gender: String,
    val profession: String,
    val systolicBP: Int,
    val diastolicBP: Int,
    val bmi: Float,
    val weightStatus: String
)