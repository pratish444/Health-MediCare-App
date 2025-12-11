package com.example.healthmedicareapp.domain.model

data class MedicalDetails(
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