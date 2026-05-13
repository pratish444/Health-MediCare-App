package com.example.healthmedicareapp.presentation.health_article

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.healthmedicareapp.domain.model.HealthArticle
import com.example.healthmedicareapp.presentation.components.ErrorMessage
import com.example.healthmedicareapp.presentation.components.MediCareTopBar

@Composable
fun HealthArticleScreen(
    onNavigateBack: () -> Unit,
    viewModel: HealthArticleViewModel = hiltViewModel()
) {
    val articles by viewModel.articles.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = { MediCareTopBar(title = "Health Articles", onBack = onNavigateBack) },
        containerColor = Color(0xFFF0F4FF)
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Search bar with debounce built into VM
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                placeholder = { Text("Search health topics...") },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = Color(0xFF1565C0)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, null, tint = Color.Gray)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1565C0), focusedLabelColor = Color(0xFF1565C0),
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )

            if (searchQuery.isNotEmpty()) {
                Text(
                    "Searching after you stop typing (debounced)...",
                    fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            error?.let {
                ErrorMessage(message = it, onRetry = { viewModel.refreshArticles() })
            }

            if (isLoading && articles.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color(0xFF1565C0))
                        Spacer(Modifier.height(12.dp))
                        Text("Loading health articles...", color = Color.Gray)
                    }
                }
            } else if (articles.isEmpty() && !isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📰", fontSize = 48.sp)
                        Spacer(Modifier.height(12.dp))
                        Text("No articles found", fontWeight = FontWeight.SemiBold, color = Color(0xFF333333))
                        Text("Check your NewsAPI key in local.properties", fontSize = 13.sp, color = Color.Gray)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { viewModel.refreshArticles() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))) {
                            Text("Retry")
                        }
                    }
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (isLoading) {
                        item { LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = Color(0xFF1565C0)) }
                    }
                    items(articles) { article ->
                        ArticleCard(article)
                    }
                }
            }
        }
    }
}

@Composable
private fun ArticleCard(article: HealthArticle) {
    val uriHandler = LocalUriHandler.current
    Card(
        modifier = Modifier.fillMaxWidth().clickable { if (article.url.isNotBlank()) uriHandler.openUri(article.url) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            // Article image
            if (!article.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = article.imageUrl, contentDescription = null,
                    modifier = Modifier.size(90.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFE3F2FD)),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(12.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = article.source, fontSize = 11.sp, color = Color(0xFF1565C0), fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = article.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color(0xFF1A1A2E),
                    maxLines = 3, overflow = TextOverflow.Ellipsis, lineHeight = 19.sp
                )
                if (!article.description.isNullOrBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = article.description, fontSize = 12.sp, color = Color.Gray,
                        maxLines = 2, overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(article.publishedAt.take(10), fontSize = 11.sp, color = Color.LightGray)
            }
        }
    }
}
