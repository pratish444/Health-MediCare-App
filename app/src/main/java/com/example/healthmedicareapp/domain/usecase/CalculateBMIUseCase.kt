package com.example.healthmedicareapp.domain.usecase

import javax.inject.Inject
import kotlin.math.pow

class CalculateBMIUseCase @Inject constructor() {
    operator fun invoke(heightCm: Int, weightKg: Int): Pair {
        val heightM = heightCm / 100.0f
        val bmi = weightKg / heightM.pow(2)

        val status = when {
            bmi < 18.5 -> "UNDERWEIGHT"
            bmi < 25.0 -> "NORMAL"
            bmi < 30.0 -> "OVER-WEIGHT"
            else -> "OBESE"
        }

        return Pair(bmi, status)
    }
}