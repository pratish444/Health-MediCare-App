package com.example.healthmedicareapp.presentation.calorie_manage

import androidx.lifecycle.ViewModel
import com.example.healthmedicareapp.domain.usecase.CalculateCaloriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CalorieManageViewModel @Inject constructor(
    private val calculateCaloriesUseCase: CalculateCaloriesUseCase
) : ViewModel() {

    private val _calorieData = MutableStateFlow(null)
    val calorieData: StateFlow = _calorieData.asStateFlow()

    fun calculateCalories(
        gender: String,
        weight: Int,
        height: Int,
        age: Int,
        activityLevel: String
    ) {
        val data = calculateCaloriesUseCase(gender, weight, height, age, activityLevel)
        _calorieData.value = data
    }
}