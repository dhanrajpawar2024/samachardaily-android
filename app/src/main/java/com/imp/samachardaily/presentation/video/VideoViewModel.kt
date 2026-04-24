package com.imp.samachardaily.presentation.video

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.imp.samachardaily.domain.model.VideoItem
import com.imp.samachardaily.domain.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VideoUiState(
    val videos: List<VideoItem> = emptyList(),
    val currentIndex: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class VideoViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val videoRepository: VideoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VideoUiState())
    val uiState: StateFlow<VideoUiState> = _uiState.asStateFlow()

    // ExoPlayer managed inside ViewModel — not in Activity/Composable
    val player: ExoPlayer = ExoPlayer.Builder(context).build().also {
        it.playWhenReady = true
    }

    init {
        fetchVideos()
    }

    fun fetchVideos(language: String? = null, categoryId: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            videoRepository.getVideos(
                page = 1,
                limit = 20,
                language = language,
                categoryId = categoryId
            ).onSuccess { videos ->
                _uiState.update { it.copy(videos = videos, isLoading = false) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }

    fun onVideoVisible(index: Int) {
        _uiState.update { it.copy(currentIndex = index) }
        val videos = _uiState.value.videos
        if (index < videos.size) {
            player.setMediaItem(MediaItem.fromUri(videos[index].videoUrl))
            player.prepare()
            player.play()

            // Fire-and-forget view increment
            viewModelScope.launch {
                videoRepository.incrementView(videos[index].id)
            }
        }
    }

    fun onLike(videoId: String) {
        viewModelScope.launch {
            videoRepository.likeVideo(videoId).onSuccess { newCount ->
                _uiState.update { state ->
                    state.copy(
                        videos = state.videos.map { v ->
                            if (v.id == videoId) v.copy(likeCount = newCount) else v
                        }
                    )
                }
            }
        }
    }

    override fun onCleared() {
        player.release()
        super.onCleared()
    }
}

