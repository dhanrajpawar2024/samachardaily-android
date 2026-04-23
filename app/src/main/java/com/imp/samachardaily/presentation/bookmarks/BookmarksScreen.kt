package com.imp.samachardaily.presentation.bookmarks

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.imp.samachardaily.R
import com.imp.samachardaily.presentation.common.ArticleCard
import com.imp.samachardaily.presentation.common.EmptyState
import com.imp.samachardaily.presentation.common.ErrorState
import com.imp.samachardaily.presentation.common.LoadingState
import com.imp.samachardaily.presentation.common.PageLoadingFooter
import com.imp.samachardaily.ui.theme.SamacharDailyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    onBack: () -> Unit,
    onArticleClick: (String) -> Unit,
    viewModel: BookmarksViewModel = hiltViewModel()
) {
    val bookmarks = viewModel.bookmarks.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.profile_bookmarks)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp)
        ) {
            when (val refreshState = bookmarks.loadState.refresh) {
                is LoadState.Loading -> item { LoadingState() }
                is LoadState.Error -> item {
                    ErrorState(
                        message = refreshState.error.message ?: stringResource(R.string.error_generic),
                        onRetry = { bookmarks.refresh() }
                    )
                }
                is LoadState.NotLoading -> {
                    if (bookmarks.itemCount == 0) {
                        item {
                            EmptyState(
                                title = stringResource(R.string.bookmarks_empty_title),
                                subtitle = stringResource(R.string.bookmarks_empty_subtitle),
                                modifier = Modifier.fillParentMaxSize()
                            )
                        }
                    }
                }
            }

            items(
                count = bookmarks.itemCount,
                key = { index -> bookmarks.peek(index)?.id ?: index }
            ) { index ->
                bookmarks[index]?.let { article ->
                    ArticleCard(
                        article = article,
                        onClick = { onArticleClick(article.id) },
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }

            when (bookmarks.loadState.append) {
                is LoadState.Loading -> item { PageLoadingFooter() }
                is LoadState.Error -> item {
                    ErrorState(
                        message = (bookmarks.loadState.append as LoadState.Error).error.message
                            ?: stringResource(R.string.error_generic),
                        onRetry = { bookmarks.retry() }
                    )
                }
                is LoadState.NotLoading -> Unit
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BookmarksScreenPreview() {
    SamacharDailyTheme {
        Box(Modifier.fillMaxSize()) {
            Text(
                text = "Bookmarks",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

