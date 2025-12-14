package com.example.healthmedicareapp.domain.usecase

import com.example.healthmedicareapp.domain.model.User
import com.example.healthmedicareapp.domain.repository.AuthRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        fullName: String,
        mobileNumber: String
    ): Result {
        return authRepository.signUp(email, password, fullName, mobileNumber)
    }
}