package com.example.healthmedicareapp.presentation.health_article

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthmedicareapp.domain.model.HealthArticle
import com.example.healthmedicareapp.domain.repository.HealthArticleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class HealthArticleViewModel @Inject constructor(
    private val healthArticleRepository: HealthArticleRepository
) : ViewModel() {

    private val _articles = MutableStateFlow<List<HealthArticle>>(emptyList())
    val articles: StateFlow<List<HealthArticle>> = _articles.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadArticles()
        refreshArticles()
        // Debounce search: wait 700ms after user stops typing before making API call
        viewModelScope.launch {
            _searchQuery
                .debounce(700L)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.isNotBlank()) {
                        searchArticles(query)
                    } else {
                        loadArticles()
                        refreshArticles()
                    }
                }
        }
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
            _error.value = null
            val result = healthArticleRepository.refreshArticles()
            if (result.isFailure) {
                _error.value = result.exceptionOrNull()?.message ?: "Failed to load articles"
            }
            _isLoading.value = false
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    private fun searchArticles(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = healthArticleRepository.searchArticles(query)
            if (result.isFailure) {
                _error.value = result.exceptionOrNull()?.message ?: "Search failed"
            }
            _isLoading.value = false
        }
    }
}