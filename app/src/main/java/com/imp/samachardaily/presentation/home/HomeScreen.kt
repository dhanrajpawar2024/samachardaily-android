package com.imp.samachardaily.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.imp.samachardaily.R
import com.imp.samachardaily.domain.model.Article
import com.imp.samachardaily.domain.model.Category
import com.imp.samachardaily.presentation.common.ArticleCard
import com.imp.samachardaily.presentation.common.ErrorState
import com.imp.samachardaily.presentation.common.LoadingState
import com.imp.samachardaily.presentation.common.PageLoadingFooter
import com.imp.samachardaily.ui.theme.SamacharDailyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onArticleClick: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val articles = viewModel.feed.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = { /* Navigate to notifications */ }) {
                        Icon(Icons.Filled.Notifications, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {

            // Category tabs
            ScrollableTabRow(
                selectedTabIndex = uiState.categories.indexOfFirst { it.id == uiState.selectedCategory?.id } + 1,
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 8.dp
            ) {
                Tab(
                    selected = uiState.selectedCategory == null,
                    onClick = { viewModel.selectCategory(null) },
                    text = { Text(stringResource(R.string.category_all)) }
                )
                uiState.categories.forEach { category ->
                    Tab(
                        selected = category.id == uiState.selectedCategory?.id,
                        onClick = { viewModel.selectCategory(category) },
                        text = { Text(category.name) }
                    )
                }
            }

            // Article list
            androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // ── Trending Now Carousel (always visible) ──────
                item {
                    TrendingSection(
                        articles = uiState.trendingArticles,
                        isLoading = uiState.isTrendingLoading,
                        error = uiState.trendingError,
                        onRetry = viewModel::retryTrending,
                        onArticleClick = onArticleClick
                    )
                }

                // Refresh state
                when (val refreshState = articles.loadState.refresh) {
                    is LoadState.Loading -> item { LoadingState() }
                    is LoadState.Error -> item {
                        ErrorState(
                            message = refreshState.error.message ?: stringResource(R.string.error_generic),
                            onRetry = { articles.refresh() }
                        )
                    }
                    is LoadState.NotLoading -> Unit
                }

                items(
                    count = articles.itemCount,
                    key = { index -> articles.peek(index)?.id ?: index }
                ) { index ->
                    articles[index]?.let { article ->
                        ArticleCard(
                            article = article,
                            onClick = { onArticleClick(article.id) },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

                // Append state
                when (val appendState = articles.loadState.append) {
                    is LoadState.Loading -> item { PageLoadingFooter() }
                    is LoadState.Error -> item {
                        ErrorState(
                            message = appendState.error.message ?: stringResource(R.string.error_generic),
                            onRetry = { articles.retry() }
                        )
                    }
                    is LoadState.NotLoading -> Unit
                }
            }
        }
    }
}

@Composable
private fun TrendingSection(
    articles: List<Article>,
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit,
    onArticleClick: (String) -> Unit
) {
    Column(modifier = Modifier.padding(top = 12.dp)) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                Icons.Filled.LocalFireDepartment,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "TRENDING NOW",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
        }

        when {
            isLoading -> Box(
                modifier = Modifier.fillMaxWidth().height(160.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(28.dp))
            }
            error != null -> Box(
                modifier = Modifier.fillMaxWidth().height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Couldn't load trending",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(onClick = onRetry) { Text("Retry") }
                }
            }
            articles.isEmpty() -> Box(
                modifier = Modifier.fillMaxWidth().height(80.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No trending articles right now",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            else -> LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(articles, key = { it.id }) { article ->
                    TrendingCard(article = article, onClick = { onArticleClick(article.id) })
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(top = 12.dp))
    }
}

@Composable
private fun TrendingCard(article: Article, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            article.thumbnailUrl?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            } ?: Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.LocalFireDepartment,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.3f),
                    modifier = Modifier.size(40.dp)
                )
            }

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = article.sourceName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    SamacharDailyTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Text("Home Screen", modifier = Modifier.padding(16.dp))
        }
    }
}
