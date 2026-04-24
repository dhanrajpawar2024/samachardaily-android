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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.imp.samachardaily.R
import com.imp.samachardaily.core.common.toCompactString
import com.imp.samachardaily.presentation.common.EmptyState
import com.imp.samachardaily.ui.theme.SamacharDailyTheme
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

/** Extract the 11-char video ID from any YouTube URL format */
fun extractYouTubeVideoId(url: String): String? {
    val patterns = listOf(
        Regex("""youtube\.com/watch\?v=([a-zA-Z0-9_-]{11})"""),
        Regex("""youtu\.be/([a-zA-Z0-9_-]{11})"""),
        Regex("""youtube\.com/embed/([a-zA-Z0-9_-]{11})"""),
    )
    for (pattern in patterns) {
        val match = pattern.find(url)
        if (match != null) return match.groupValues[1]
    }
    return null
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun ShortVideoScreen(
    viewModel: VideoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { uiState.videos.size })

    LaunchedEffect(pagerState.currentPage) {
        viewModel.onVideoVisible(pagerState.currentPage)
    }

    if (uiState.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
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
        val videoId = extractYouTubeVideoId(video.videoUrl)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            if (videoId != null && page == pagerState.currentPage) {
                YouTubeVideoPlayer(
                    videoId = videoId,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Overlay: title + channel + view count
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 3
                )
                Spacer(Modifier.height(4.dp))
                video.authorName?.let {
                    Text(
                        text = it,
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

@Composable
private fun YouTubeVideoPlayer(
    videoId: String,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(videoId) {
        onDispose { /* lifecycle observer removed by YouTubePlayerView itself on destroy */ }
    }

    AndroidView(
        factory = { context ->
            YouTubePlayerView(context).apply {
                enableAutomaticInitialization = false
                lifecycleOwner.lifecycle.addObserver(this)

                val options = IFramePlayerOptions.Builder()
                    .controls(1)   // show native YT controls
                    .autoplay(1)
                    .build()

                initialize(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.loadVideo(videoId, 0f)
                    }
                }, options)
            }
        },
        modifier = modifier
    )
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

