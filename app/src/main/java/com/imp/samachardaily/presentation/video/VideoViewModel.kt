package com.imp.samachardaily.presentation.video

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.imp.samachardaily.domain.model.VideoItem
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class VideoUiState(
    val videos: List<VideoItem> = emptyList(),
    val currentIndex: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class VideoViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(VideoUiState())
    val uiState: StateFlow<VideoUiState> = _uiState.asStateFlow()

    // ExoPlayer managed inside ViewModel — not in Activity/Composable
    val player: ExoPlayer = ExoPlayer.Builder(context).build().also {
        it.playWhenReady = true
    }

    fun onVideoVisible(index: Int) {
        _uiState.update { it.copy(currentIndex = index) }
        val videos = _uiState.value.videos
        if (index < videos.size) {
            player.setMediaItem(MediaItem.fromUri(videos[index].videoUrl))
            player.prepare()
            player.play()
        }
    }

    fun loadVideos(videos: List<VideoItem>) {
        _uiState.update { it.copy(videos = videos) }
    }

    override fun onCleared() {
        player.release()
        super.onCleared()
    }
}

