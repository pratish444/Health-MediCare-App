package com.example.healthmedicareapp.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun formatTimestamp(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }

    fun formatDuration(millis: Long): String {
        val hours = millis / (1000 * 60 * 60)
        val minutes = (millis % (1000 * 60 * 60)) / (1000 * 60)
        val seconds = (millis % (1000 * 60)) / 1000
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}