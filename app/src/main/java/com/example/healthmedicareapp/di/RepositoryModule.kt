package com.example.healthmedicareapp.di

import com.example.healthmedicareapp.data.repository.*
import com.example.healthmedicareapp.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindMedicalRepository(
        medicalRepositoryImpl: MedicalRepositoryImpl
    ): MedicalRepository

    @Binds
    @Singleton
    abstract fun bindSleepTrackRepository(
        sleepTrackRepositoryImpl: SleepTrackRepositoryImpl
    ): SleepTrackRepository

    @Binds
    @Singleton
    abstract fun bindHealthArticleRepository(
        healthArticleRepositoryImpl: HealthArticleRepositoryImpl
    ): HealthArticleRepository
}