package com.imp.samachardaily.presentation.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.imp.samachardaily.R
import com.imp.samachardaily.presentation.common.ArticleCard
import com.imp.samachardaily.presentation.common.EmptyState
import com.imp.samachardaily.presentation.common.ErrorState
import com.imp.samachardaily.presentation.common.PageLoadingFooter
import com.imp.samachardaily.ui.theme.SamacharDailyTheme

@Composable
fun SearchScreen(
    onArticleClick: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val results = viewModel.searchResults.collectAsLazyPagingItems()
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier = Modifier.fillMaxSize()) {
        // Search bar
        OutlinedTextField(
            value = uiState.query,
            onValueChange = viewModel::onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            placeholder = { Text(stringResource(R.string.search_hint)) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            trailingIcon = {
                if (uiState.query.isNotEmpty()) {
                    IconButton(onClick = viewModel::clearQuery) {
                        Icon(Icons.Filled.Clear, contentDescription = null)
                    }
                }
            },
            singleLine = true,
            shape = MaterialTheme.shapes.extraLarge,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() })
        )

        // Language filter row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(null to "All", "en" to "English", "hi" to "हिन्दी", "mr" to "मराठी")
                .forEach { (code, label) ->
                    FilterChip(
                        selected = uiState.selectedLanguage == code,
                        onClick = { viewModel.setLanguageFilter(code) },
                        label = { Text(label) }
                    )
                }
        }

        Spacer(Modifier.height(8.dp))

        // Results
        when {
            uiState.query.isBlank() -> EmptyState(
                title = stringResource(R.string.search_empty_title),
                subtitle = stringResource(R.string.search_empty_subtitle),
                modifier = Modifier.fillMaxSize()
            )
            else -> androidx.compose.foundation.lazy.LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (val refreshState = results.loadState.refresh) {
                    is LoadState.Loading -> item {
                        com.imp.samachardaily.presentation.common.LoadingState()
                    }
                    is LoadState.Error -> item {
                        ErrorState(
                            message = refreshState.error.message ?: stringResource(R.string.error_generic),
                            onRetry = { results.refresh() }
                        )
                    }
                    is LoadState.NotLoading -> {
                        if (results.itemCount == 0) {
                            item {
                                EmptyState(
                                    title = stringResource(R.string.search_no_results_title),
                                    subtitle = stringResource(R.string.search_no_results_subtitle)
                                )
                            }
                        }
                    }
                }

                items(
                    count = results.itemCount,
                    key = { index -> results.peek(index)?.id ?: index }
                ) { index ->
                    results[index]?.let { article ->
                        ArticleCard(article = article, onClick = { onArticleClick(article.id) })
                    }
                }

                if (results.loadState.append is LoadState.Loading) {
                    item { PageLoadingFooter() }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenPreview() {
    SamacharDailyTheme {
        Box(Modifier.fillMaxSize()) { Text("Search Screen", Modifier.padding(16.dp)) }
    }
}

