package com.imp.samachardaily.presentation.video

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.ui.PlayerView
import com.imp.samachardaily.R
import com.imp.samachardaily.core.common.toCompactString
import com.imp.samachardaily.presentation.common.EmptyState
import com.imp.samachardaily.ui.theme.SamacharDailyTheme

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun ShortVideoScreen(
    viewModel: VideoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { uiState.videos.size })

    // When page changes, notify ViewModel to switch ExoPlayer media
    LaunchedEffect(pagerState.currentPage) {
        viewModel.onVideoVisible(pagerState.currentPage)
    }

    if (uiState.videos.isEmpty()) {
        EmptyState(
            title = stringResource(R.string.video_empty_title),
            subtitle = stringResource(R.string.video_empty_subtitle),
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    VerticalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        val video = uiState.videos[page]
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // ExoPlayer view — only attached to live player for the current visible page
            if (page == pagerState.currentPage) {
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = viewModel.player
                            useController = false
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Overlay: title + stats
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(Modifier.height(4.dp))
                video.authorName?.let {
                    Text(
                        text = "@$it",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${video.viewCount.toCompactString()} views",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ShortVideoPreview() {
    SamacharDailyTheme {
        Box(Modifier.fillMaxSize().background(Color.Black)) {
            Text("Short Video Feed", color = Color.White, modifier = Modifier.padding(16.dp))
        }
    }
}

