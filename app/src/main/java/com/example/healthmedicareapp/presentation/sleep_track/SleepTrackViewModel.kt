package com.example.healthmedicareapp.presentation.sleep_track

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthmedicareapp.domain.model.SleepTrack
import com.example.healthmedicareapp.domain.repository.AuthRepository
import com.example.healthmedicareapp.domain.repository.SleepTrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SleepTrackViewModel @Inject constructor(
    private val sleepTrackRepository: SleepTrackRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _currentTrack = MutableStateFlow<SleepTrack?>(null)
    val currentTrack: StateFlow<SleepTrack?> = _currentTrack.asStateFlow()

    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()

    init {
        checkCurrentTracking()
    }

    private fun checkCurrentTracking() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser().firstOrNull() ?: return@launch
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val today = dateFormat.format(Date())
            val track = sleepTrackRepository.getSleepTrackByDate(user.userId, today)
            _currentTrack.value = track
            _isTracking.value = track != null && track.endTime == null
        }
    }

    fun startTracking() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser().firstOrNull() ?: return@launch
            val startTime = System.currentTimeMillis()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = dateFormat.format(Date())
            val result = sleepTrackRepository.startSleepTracking(user.userId, startTime, date)
            if (result.isSuccess) {
                _isTracking.value = true
                checkCurrentTracking()
            }
        }
    }

    fun stopTracking() {
        viewModelScope.launch {
            val track = _currentTrack.value ?: return@launch
            val endTime = System.currentTimeMillis()
            val result = sleepTrackRepository.stopSleepTracking(track.id, endTime)
            if (result.isSuccess) {
                _isTracking.value = false
                checkCurrentTracking()
            }
        }
    }
}