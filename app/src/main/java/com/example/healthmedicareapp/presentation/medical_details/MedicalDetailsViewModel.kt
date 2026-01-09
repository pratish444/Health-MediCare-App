package com.example.healthmedicareapp.presentation.medical_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthmedicareapp.domain.model.MedicalDetails
import com.example.healthmedicareapp.domain.repository.MedicalRepository
import com.example.healthmedicareapp.domain.usecase.CalculateBMIUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedicalDetailsViewModel @Inject constructor(
    private val medicalRepository: MedicalRepository,
    private val calculateBMIUseCase: CalculateBMIUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MedicalDetailsUiState.Idle)
    val uiState: StateFlow = _uiState.asStateFlow()

    fun saveMedicalDetails(
        userId: String,
        height: Int,
        weight: Int,
        age: Int,
        bloodGroup: String,
        location: String,
        gender: String,
        profession: String,
        systolicBP: Int,
        diastolicBP: Int
    ) {
        viewModelScope.launch {
            _uiState.value = MedicalDetailsUiState.Loading

            val (bmi, weightStatus) = calculateBMIUseCase(height, weight)

            val details = MedicalDetails(
                userId = userId,
                height = height,
                weight = weight,
                age = age,
                bloodGroup = bloodGroup,
                location = location,
                gender = gender,
                profession = profession,
                systolicBP = systolicBP,
                diastolicBP = diastolicBP,
                bmi = bmi,
                weightStatus = weightStatus
            )

            val result = medicalRepository.saveMedicalDetails(details)

            _uiState.value = (if (result.isSuccess) {
                MedicalDetailsUiState.Success
            } else {
                MedicalDetailsUiState.Error(result.exceptionOrNull()?.message ?: "Failed to save")
            }) as MedicalDetailsUiState.Idle
        }
    }
}

sealed class MedicalDetailsUiState {
    object Idle : MedicalDetailsUiState()
    object Loading : MedicalDetailsUiState()
    object Success : MedicalDetailsUiState()
    data class Error(val message: String) : MedicalDetailsUiState()
}