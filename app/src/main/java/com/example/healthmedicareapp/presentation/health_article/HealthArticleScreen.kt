package com.example.healthmedicareapp.presentation.health_article

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
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
    onNavigateBack: (() -> Unit)? = null,
    viewModel: HealthArticleViewModel = hiltViewModel()
) {
    val articles    by viewModel.articles.collectAsState()
    val isLoading   by viewModel.isLoading.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val error       by viewModel.error.collectAsState()

    Scaffold(
        topBar         = {
            if (onNavigateBack != null) MediCareTopBar(title = "Health Articles", onBack = onNavigateBack)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // Inline header when used as tab
            if (onNavigateBack == null) {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier          = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier         = Modifier
                            .size(44.dp)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF1565C0), Color(0xFF42A5F5)),
                                    start = Offset(0f, 0f),
                                    end   = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                                ),
                                RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Article, null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Health Articles", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = MaterialTheme.colorScheme.onBackground)
                        Text("Stay informed, stay healthy", fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f))
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            // ── Search bar ────────────────────────────────────────────────────
            OutlinedTextField(
                value         = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                placeholder   = { Text("Search health topics…") },
                leadingIcon   = { Icon(Icons.Rounded.Search, null, tint = MaterialTheme.colorScheme.primary) },
                trailingIcon  = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(Icons.Rounded.Close, null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        }
                    }
                },
                modifier      = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                shape         = RoundedCornerShape(16.dp),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = MaterialTheme.colorScheme.primary,
                    focusedLabelColor    = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedContainerColor   = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                singleLine = true
            )

            error?.let { ErrorMessage(message = it, onRetry = { viewModel.refreshArticles() }) }

            if (isLoading && articles.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(12.dp))
                        Text("Loading health articles…", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                    }
                }
            } else if (articles.isEmpty() && !isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Rounded.Article, null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), modifier = Modifier.size(64.dp))
                        Spacer(Modifier.height(12.dp))
                        Text("No articles found", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
                        Text("Check your NewsAPI key in local.properties", fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f))
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { viewModel.refreshArticles() }) { Text("Retry") }
                    }
                }
            } else {
                LazyColumn(
                    contentPadding      = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (isLoading) {
                        item { LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.primary) }
                    }
                    items(articles) { article -> ArticleCard(article) }
                }
            }
        }
    }
}

@Composable
private fun ArticleCard(article: HealthArticle) {
    val uriHandler = LocalUriHandler.current
    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .clickable { if (article.url.isNotBlank()) uriHandler.openUri(article.url) },
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            if (!article.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model              = article.imageUrl,
                    contentDescription = null,
                    modifier           = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale       = ContentScale.Crop
                )
                Spacer(Modifier.width(12.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = article.source,
                    fontSize   = 11.sp,
                    color      = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text       = article.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 14.sp,
                    color      = MaterialTheme.colorScheme.onSurface,
                    maxLines   = 3,
                    overflow   = TextOverflow.Ellipsis,
                    lineHeight = 19.sp
                )
                if (!article.description.isNullOrBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text     = article.description,
                        fontSize = 12.sp,
                        color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Schedule, null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f), modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(3.dp))
                    Text(article.publishedAt.take(10), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                }
            }
        }
    }
}
