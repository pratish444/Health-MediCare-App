package com.example.healthmedicareapp.presentation.auth.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthmedicareapp.domain.usecase.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrationUiState.Idle)
    val uiState: StateFlow = _uiState.asStateFlow()

    fun signUp(email: String, password: String, fullName: String, mobileNumber: String) {
        viewModelScope.launch {
            _uiState.value = RegistrationUiState.Loading

            val result = signUpUseCase(email, password, fullName, mobileNumber)

            _uiState.value = if (result.isSuccess) {
                RegistrationUiState.Success
            } else {
                RegistrationUiState.Error(result.exceptionOrNull()?.message ?: "Registration failed")
            }
        }
    }

    fun resetState() {
        _uiState.value = RegistrationUiState.Idle
    }
}

sealed class RegistrationUiState {
    object Idle : RegistrationUiState()
    object Loading : RegistrationUiState()
    object Success : RegistrationUiState()
    data class Error(val message: String) : RegistrationUiState()
}