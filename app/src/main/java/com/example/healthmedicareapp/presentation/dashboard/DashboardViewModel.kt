package com.example.healthmedicareapp.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthmedicareapp.domain.model.MedicalDetails
import com.example.healthmedicareapp.domain.model.User
import com.example.healthmedicareapp.domain.repository.AuthRepository
import com.example.healthmedicareapp.domain.repository.MedicalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val medicalRepository: MedicalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState.Loading)
    val uiState: StateFlow = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            authRepository.getCurrentUser().collect { user ->
                if (user != null) {
                    medicalRepository.getMedicalDetails(user.userId).collect { medicalDetails ->
                        _uiState.value = DashboardUiState.Success(user, medicalDetails)
                    }
                } else {
                    _uiState.value = DashboardUiState.Error("User not found")
                }
            }
        }
    }
}

sealed class DashboardUiState {
    object Loading : DashboardUiState()
    data class Success(val user: User, val medicalDetails: MedicalDetails?) : DashboardUiState()
    data class Error(val message: String) : DashboardUiState()
}