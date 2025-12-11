package com.example.healthmedicareapp.domain.usecase

import javax.inject.Inject

class CalculateCaloriesUseCase @Inject constructor() {

    data class CalorieData(
        val bmr: Int,
        val maintenanceCalories: Int,
        val weightLossCalories: Int,
        val weightGainCalories: Int
    )

    operator fun invoke(
        gender: String,
        weightKg: Int,
        heightCm: Int,
        age: Int,
        activityLevel: String
    ): CalorieData {
        // Calculate BMR using Mifflin-St Jeor Equation
        val bmr = if (gender.equals("Male", ignoreCase = true)) {
            (10 * weightKg + 6.25 * heightCm - 5 * age + 5).toInt()
        } else {
            (10 * weightKg + 6.25 * heightCm - 5 * age - 161).toInt()
        }

        // Activity multipliers
        val activityMultiplier = when (activityLevel) {
            "Sedentary (little/no exercise)" -> 1.2f
            "Lightly Active (1-3 day/week)" -> 1.375f
            "Moderately Active (3-5 day/week)" -> 1.55f
            "Very Active (6-7 day/week)" -> 1.725f
            "Extremely Active (athlete)" -> 1.9f
            else -> 1.375f
        }

        val maintenanceCalories = (bmr * activityMultiplier).toInt()
        val weightLossCalories = maintenanceCalories - 500 // 0.5 kg per week
        val weightGainCalories = maintenanceCalories + 500 // 0.5 kg per week

        return CalorieData(
            bmr = bmr,
            maintenanceCalories = maintenanceCalories,
            weightLossCalories = weightLossCalories,
            weightGainCalories = weightGainCalories
        )
    }
}