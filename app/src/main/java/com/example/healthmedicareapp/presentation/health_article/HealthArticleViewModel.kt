package com.example.healthmedicareapp.presentation.health_article

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthmedicareapp.domain.model.HealthArticle
import com.example.healthmedicareapp.domain.repository.HealthArticleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HealthArticleViewModel @Inject constructor(
    private val healthArticleRepository: HealthArticleRepository
) : ViewModel() {

    private val _articles = MutableStateFlow<List>(emptyList())
    val articles: StateFlow<List> = _articles.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow = _isLoading.asStateFlow()

    init {
        loadArticles()
        refreshArticles()
    }

    private fun loadArticles() {
        viewModelScope.launch {
            healthArticleRepository.getHealthArticles().collect { articles ->
                _articles.value = articles
            }
        }
    }

    fun refreshArticles() {
        viewModelScope.launch {
            _isLoading.value = true
            healthArticleRepository.refreshArticles()
            _isLoading.value = false
        }
    }
}