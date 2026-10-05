package com.example.musicplayer.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.data.model.LyricLine
import com.example.musicplayer.data.model.MusicTrack
import com.example.musicplayer.data.model.SavedPlaylist
import com.example.musicplayer.data.model.SubjectCategory
import com.example.musicplayer.data.model.SubjectPlaylist
import com.example.musicplayer.data.repository.LyricsRepository
import com.example.musicplayer.data.repository.PlaylistRepository
import com.example.musicplayer.data.repository.SubjectPlaylistRepository
import com.example.musicplayer.player.YouTubePlayerListener
import com.example.musicplayer.player.YouTubePlayerManager
import com.example.musicplayer.service.MusicPlaybackService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class PlayerTab {
    PLAYER,
    SUBJECTS,
    SONGS,
    SEARCH
}

data class PlayerUiState(
    val currentTab: PlayerTab = PlayerTab.PLAYER,
    val isPlaying: Boolean = false,
    val title: String = "പ്ലേലിസ്റ്റ് ലോഡ് ചെയ്യുന്നു...",
    val artist: String = "YouTube Music",
    val currentVideoId: String = "",
    val thumbnailUrl: String = "",
    val currentPlaylistId: String = PlaylistRepository.DEFAULT_PLAYLIST_ID,
    val currentIndex: Int = 0,
    val tracks: List<MusicTrack> = emptyList(),
    val currentTimeSeconds: Float = 0f,
    val durationSeconds: Float = 0f,
    val isReady: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val recentPlaylists: List<String> = emptyList(),
    val selectedSubjectCategoryId: String? = null,
    val searchQuery: String = "",
    val searchResults: List<SubjectPlaylist> = emptyList(),
    val trackSearchQuery: String = "" ,
    val lyrics: List<LyricLine> = emptyList(),
    val activeLyricIndex: Int = 0,
    val showLyricsPanel: Boolean = false
)

class MusicPlayerViewModel(application: Application) : AndroidViewModel(application), YouTubePlayerListener {

    private val playlistRepo = PlaylistRepository(application)
    private val subjectRepo = SubjectPlaylistRepository()
    private val lyricsRepo = LyricsRepository()
    private var playerManager: YouTubePlayerManager? = null

    val categories: List<SubjectCategory> = subjectRepo.categories
    val presetPlaylists: List<SavedPlaylist> = playlistRepo.defaultPlaylists

    private val _uiState = MutableStateFlow(
        PlayerUiState(
            currentPlaylistId = playlistRepo.getSavedPlaylistId(),
            recentPlaylists = playlistRepo.getPlaylistHistory(),
            selectedSubjectCategoryId = subjectRepo.categories.firstOrNull()?.id
        )
    )
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    init {
        // Register listener for notification controls
        MusicPlaybackService.playbackActionListener = { action ->
            when (action) {
                MusicPlaybackService.ACTION_TOGGLE -> togglePlay()
                MusicPlaybackService.ACTION_PLAY -> if (!_uiState.value.isPlaying) togglePlay()
                MusicPlaybackService.ACTION_PAUSE -> if (_uiState.value.isPlaying) togglePlay()
                MusicPlaybackService.ACTION_NEXT -> nextTrack()
                MusicPlaybackService.ACTION_PREV -> previousTrack()
            }
        }
    }

    fun setPlayerManager(manager: YouTubePlayerManager) {
        this.playerManager = manager
    }

    fun setTab(tab: PlayerTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun selectSubjectCategory(categoryId: String) {
        _uiState.update { it.copy(selectedSubjectCategoryId = categoryId) }
    }

    fun updateSearchQuery(query: String) {
        val results = if (query.isNotBlank()) subjectRepo.searchPlaylists(query) else emptyList()
        _uiState.update {
            it.copy(
                searchQuery = query,
                searchResults = results
            )
        }
    }

    fun updateTrackSearchQuery(query: String) {
        _uiState.update { it.copy(trackSearchQuery = query) }
    }

    fun playSubjectPlaylist(playlist: SubjectPlaylist) {
        if (playlist.isSearchQuery) {
            searchAndPlay(playlist.searchQuery, playlist.title)
        } else {
            loadNewPlaylist(playlist.id, playlist.title)
        }
        setTab(PlayerTab.PLAYER)
    }

    fun searchAndPlay(query: String, customTitle: String? = null) {
        val displayTitle = customTitle ?: "തിരയുന്നു: $query"
        _uiState.update {
            it.copy(
                title = displayTitle,
                artist = "YouTube Music",
                isLoading = true,
                currentTab = PlayerTab.PLAYER
            )
        }
        playerManager?.searchAndPlay(query)
    }

    fun toggleLyricsPanel() {
        _uiState.update { it.copy(showLyricsPanel = !it.showLyricsPanel) }
    }

    override fun onPlayerReady() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isReady = true,
                    isLoading = false,
                    title = if (it.title == "പ്ലേലിസ്റ്റ് ലോഡ് ചെയ്യുന്നു...") "പ്ലേ ചെയ്യാൻ തയ്യാറാണ്" else it.title
                )
            }
            updateForegroundService()
        }
    }

    override fun onPlayerStateChange(state: Int) {
        viewModelScope.launch {
            when (state) {
                1 -> { // Playing
                    _uiState.update { it.copy(isPlaying = true, isLoading = false) }
                }
                2, 0 -> { // Paused or Ended
                    _uiState.update { it.copy(isPlaying = false, isLoading = false) }
                }
                3 -> { // Buffering
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
            updateForegroundService()
        }
    }

    override fun onTrackDetails(
        title: String,
        artist: String,
        videoId: String,
        duration: Float,
        currentTime: Float,
        index: Int
    ) {
        viewModelScope.launch {
            val artUrl = if (videoId.isNotEmpty()) {
                "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
            } else ""

            val titleToUse = if (title.isNotBlank()) title else _uiState.value.title
            val resolvedLyrics = lyricsRepo.getSyncedLyrics(titleToUse, duration)

            _uiState.update { current ->
                val updatedTracks = if (current.tracks.isNotEmpty()) {
                    current.tracks.mapIndexed { idx, track ->
                        if (idx == index) {
                            track.copy(
                                title = if (title.isNotBlank()) title else track.title,
                                artist = if (artist.isNotBlank()) artist else track.artist,
                                videoId = if (videoId.isNotBlank()) videoId else track.videoId,
                                durationSeconds = duration,
                                isPlaying = true
                            )
                        } else {
                            track.copy(isPlaying = false)
                        }
                    }
                } else {
                    current.tracks
                }

                current.copy(
                    title = titleToUse,
                    artist = if (artist.isNotBlank()) artist else current.artist,
                    currentVideoId = videoId,
                    thumbnailUrl = artUrl,
                    durationSeconds = duration,
                    currentTimeSeconds = currentTime,
                    currentIndex = index,
                    tracks = updatedTracks,
                    isLoading = false,
                    lyrics = resolvedLyrics,
                    activeLyricIndex = 0
                )
            }
            updateActiveLyricIndex(currentTime)
            updateForegroundService()
        }
    }

    override fun onPlaylistLoaded(trackVideoIds: List<String>) {
        viewModelScope.launch {
            val initialTracks = trackVideoIds.mapIndexed { index, vid ->
                MusicTrack(
                    index = index,
                    videoId = vid,
                    title = "🎵 Track ${index + 1}",
                    artist = "YouTube Music",
                    isPlaying = (index == _uiState.value.currentIndex)
                )
            }
            _uiState.update {
                it.copy(
                    tracks = if (initialTracks.isNotEmpty()) initialTracks else it.tracks,
                    isLoading = false
                )
            }

            // Asynchronously resolve real titles for the tracks
            trackVideoIds.take(20).forEachIndexed { idx, vid ->
                launch {
                    val resolvedTitle = subjectRepo.fetchVideoTitle(vid)
                    if (resolvedTitle.isNotBlank()) {
                        _uiState.update { current ->
                            val updated = current.tracks.mapIndexed { i, t ->
                                if (i == idx && t.title.startsWith("🎵 Track")) {
                                    t.copy(title = resolvedTitle)
                                } else t
                            }
                            current.copy(tracks = updated)
                        }
                    }
                }
            }
        }
    }

    override fun onPlaybackProgress(currentTime: Float, duration: Float) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    currentTimeSeconds = currentTime,
                    durationSeconds = if (duration > 0f) duration else it.durationSeconds
                )
            }
            updateActiveLyricIndex(currentTime)
        }
    }

    private fun updateActiveLyricIndex(currentTime: Float) {
        val list = _uiState.value.lyrics
        if (list.isEmpty()) return
        var activeIdx = 0
        for (i in list.indices) {
            if (currentTime >= list[i].timestampSeconds) {
                activeIdx = i
            } else {
                break
            }
        }
        _uiState.update { it.copy(activeLyricIndex = activeIdx) }
    }

    override fun onPlayerError(errorCode: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    errorMessage = "Track error ($errorCode). Moving to next...",
                    isLoading = false
                )
            }
        }
    }

    fun togglePlay() {
        if (_uiState.value.isPlaying) {
            playerManager?.pause()
        } else {
            playerManager?.play()
        }
    }

    fun nextTrack() {
        playerManager?.next()
    }

    fun previousTrack() {
        playerManager?.previous()
    }

    fun playTrack(index: Int) {
        _uiState.update { it.copy(currentIndex = index) }
        playerManager?.playTrackAt(index)
    }

    fun seekTo(seconds: Float) {
        _uiState.update { it.copy(currentTimeSeconds = seconds) }
        playerManager?.seekTo(seconds)
    }

    fun loadNewPlaylist(input: String, customTitle: String? = null) {
        val playlistId = playlistRepo.parsePlaylistId(input)
        if (playlistId.isNotBlank()) {
            playlistRepo.savePlaylistId(playlistId)
            _uiState.update {
                it.copy(
                    currentPlaylistId = playlistId,
                    isLoading = true,
                    title = customTitle ?: "പ്ലേലിസ്റ്റ് ലോഡ് ചെയ്യുന്നു...",
                    recentPlaylists = playlistRepo.getPlaylistHistory()
                )
            }
            playerManager?.loadPlaylist(playlistId)
        }
    }

    fun selectPreset(playlist: SavedPlaylist) {
        loadNewPlaylist(playlist.id, playlist.title)
    }

    private fun updateForegroundService() {
        val state = _uiState.value
        try {
            MusicPlaybackService.updatePlayback(
                getApplication(),
                state.title,
                state.artist,
                state.isPlaying
            )
        } catch (_: Exception) { }
    }

    override fun onCleared() {
        super.onCleared()
        MusicPlaybackService.playbackActionListener = null
    }
}
