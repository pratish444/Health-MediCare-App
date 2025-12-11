package com.example.healthmedicareapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.healthmedicareapp.data.local.dao.*
import com.example.healthmedicareapp.data.local.entities.*

@Database(
    entities = [
        UserEntity::class,
        MedicalDetailsEntity::class,
        SleepTrackEntity::class,
        HealthArticleEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun medicalDetailsDao(): MedicalDetailsDao
    abstract fun sleepTrackDao(): SleepTrackDao
    abstract fun healthArticleDao(): HealthArticleDao
}