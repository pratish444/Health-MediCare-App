package com.example.healthmedicareapp.domain.usecase

import com.example.healthmedicareapp.domain.model.User
import com.example.healthmedicareapp.domain.repository.AuthRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return authRepository.signIn(email, password)
    }
}