package com.imp.samachardaily.presentation.article

import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.imp.samachardaily.R
import com.imp.samachardaily.core.common.toRelativeTimeString
import com.imp.samachardaily.presentation.common.ErrorState
import com.imp.samachardaily.presentation.common.LoadingState
import com.imp.samachardaily.ui.theme.SamacharDailyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("UNUSED_PARAMETER")
@Composable
fun ArticleDetailScreen(
    articleId: String,
    onBack: () -> Unit,
    viewModel: ArticleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    fun openSource(url: String) {
        val colorSchemeParams = CustomTabColorSchemeParams.Builder().build()
        val customTabsIntent = CustomTabsIntent.Builder()
            .setDefaultColorSchemeParams(colorSchemeParams)
            .setShowTitle(true)
            .setShareState(CustomTabsIntent.SHARE_STATE_ON)
            .setUrlBarHidingEnabled(true)
            .build()
        customTabsIntent.launchUrl(context, Uri.parse(url))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (uiState is ArticleUiState.Success) {
                        Text(
                            text = (uiState as ArticleUiState.Success).article.sourceName,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    if (uiState is ArticleUiState.Success) {
                        val state = uiState as ArticleUiState.Success

                        // Follow Source
                        IconButton(onClick = viewModel::toggleFollowSource) {
                            Icon(
                                imageVector = if (state.isSourceFollowed)
                                    Icons.Filled.NotificationsActive
                                else Icons.Filled.NotificationsNone,
                                contentDescription = if (state.isSourceFollowed) "Unfollow source" else "Follow source",
                                tint = if (state.isSourceFollowed) MaterialTheme.colorScheme.primary
                                       else LocalContentColor.current
                            )
                        }

                        // Bookmark
                        IconButton(onClick = viewModel::toggleBookmark) {
                            Icon(
                                imageVector = if (state.isBookmarked) Icons.Filled.Bookmark
                                              else Icons.Outlined.Bookmark,
                                contentDescription = stringResource(
                                    if (state.isBookmarked) R.string.action_remove_bookmark
                                    else R.string.action_bookmark
                                )
                            )
                        }

                        // Share
                        IconButton(onClick = {
                            viewModel.logShare()
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, state.article.title)
                                putExtra(Intent.EXTRA_TEXT, state.article.sourceUrl)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, null))
                        }) {
                            Icon(
                                Icons.Filled.Share,
                                contentDescription = stringResource(R.string.action_share)
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (val state = uiState) {
                is ArticleUiState.Loading -> LoadingState(Modifier.fillMaxSize())
                is ArticleUiState.Error   -> ErrorState(
                    message = state.message,
                    modifier = Modifier.fillMaxSize()
                )
                is ArticleUiState.Success -> {
                                    val article = state.article
                                    val bodyText = article.content?.takeIf { it.isNotBlank() }
                                        ?: article.summary.takeIf { it.isNotBlank() }
                                    val scaledBodyStyle = MaterialTheme.typography.bodyLarge.copy(
                                        fontSize = MaterialTheme.typography.bodyLarge.fontSize * state.textSize
                                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(innerPadding)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        article.thumbnailUrl?.let { imageUrl ->
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = article.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                                    .clip(MaterialTheme.shapes.large),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Text(
                            text = article.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Surface(
                                                color = MaterialTheme.colorScheme.primaryContainer,
                                                shape = MaterialTheme.shapes.small
                                            ) {
                                                Text(
                                                    text = article.sourceName,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                                )
                                            }

                                            if (state.isSourceFollowed) {
                                                Surface(
                                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                                    shape = MaterialTheme.shapes.small
                                                ) {
                                                    Text(
                                                        text = "Following",
                                                        style = MaterialTheme.typography.labelMedium,
                                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                                    )
                                                }
                                            }

                                            Text(
                                                text = article.publishedAt.toRelativeTimeString(),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(top = 8.dp)
                                            )
                                        }

                        article.author?.takeIf { it.isNotBlank() }?.let { author ->
                            Text(
                                text = stringResource(R.string.article_by_author, author),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = stringResource(R.string.article_quick_read),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(Modifier.height(8.dp))
                                        Text(
                                            text = bodyText ?: stringResource(R.string.article_content_unavailable),
                                            style = scaledBodyStyle,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                            }
                        }

                        Button(
                            onClick = { openSource(article.sourceUrl) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.OpenInBrowser, contentDescription = null)
                            Spacer(Modifier.size(8.dp))
                            Text(stringResource(R.string.article_read_on_source))
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ArticleDetailPreview() {
    SamacharDailyTheme {
        Box(Modifier.fillMaxSize()) {
            Text("Article Detail Screen", Modifier.padding(16.dp))
        }
    }
}

