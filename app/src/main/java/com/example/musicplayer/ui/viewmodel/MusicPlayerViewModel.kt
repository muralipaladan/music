package com.example.musicplayer.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.data.model.LyricLine
import com.example.musicplayer.data.model.MusicTrack
import com.example.musicplayer.data.model.SavedPlaylist
import com.example.musicplayer.data.model.SubjectCategory
import com.example.musicplayer.data.model.SubjectPlaylist
import com.example.musicplayer.data.repository.IndexedSongsDatabase
import com.example.musicplayer.data.repository.LyricsRepository
import com.example.musicplayer.data.repository.PlaylistRepository
import com.example.musicplayer.data.repository.SubjectPlaylistRepository
import com.example.musicplayer.player.YouTubePlayerListener
import com.example.musicplayer.player.YouTubePlayerManager
import com.example.musicplayer.service.MusicPlaybackService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class PlayerTab {
    PLAYER,
    SUBJECTS,
    SONGS,
    SEARCH
}

enum class SongsFilterMode {
    CURRENT_PLAYLIST,
    ALL_INDEXED,
    RECENTLY_PLAYED
}

data class PlayerUiState(
    val currentTab: PlayerTab = PlayerTab.PLAYER,
    val isPlaying: Boolean = false,
    val title: String = "Illuminati (Aavesham)",
    val artist: String = "Sushin Shyam, Dabzee",
    val currentVideoId: String = "a3Ue-LN5B9U",
    val thumbnailUrl: String = "https://img.youtube.com/vi/a3Ue-LN5B9U/hqdefault.jpg",
    val currentPlaylistId: String = PlaylistRepository.DEFAULT_PLAYLIST_ID,
    val currentIndex: Int = 0,
    val tracks: List<MusicTrack> = emptyList(),
    val indexedSongs: List<MusicTrack> = emptyList(),
    val recentSongs: List<MusicTrack> = emptyList(),
    val songsFilterMode: SongsFilterMode = SongsFilterMode.CURRENT_PLAYLIST,
    val songsLogSearchQuery: String = "",
    val currentTimeSeconds: Float = 0f,
    val durationSeconds: Float = 0f,
    val isReady: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val recentPlaylists: List<String> = emptyList(),
    val selectedSubjectCategoryId: String? = null,
    val searchQuery: String = "",
    val searchResults: List<SubjectPlaylist> = emptyList(),
    val trackSearchQuery: String = "",
    val lyrics: List<LyricLine> = emptyList(),
    val activeLyricIndex: Int = 0,
    val showLyricsPanel: Boolean = false
)

class MusicPlayerViewModel(application: Application) : AndroidViewModel(application), YouTubePlayerListener {

    private val playlistRepo = PlaylistRepository(application)
    private val subjectRepo = SubjectPlaylistRepository()
    private val lyricsRepo = LyricsRepository()
    private val indexedDb = IndexedSongsDatabase(application)
    private var playerManager: YouTubePlayerManager? = null

    val categories: List<SubjectCategory> = subjectRepo.categories
    val presetPlaylists: List<SavedPlaylist> = playlistRepo.defaultPlaylists

    private val initialDefaultTracks = subjectRepo.categories.firstOrNull()?.playlists?.firstOrNull()?.initialTracks ?: emptyList()
    private val initialFirstTrack = initialDefaultTracks.firstOrNull()

    private val _uiState = MutableStateFlow(
        PlayerUiState(
            currentPlaylistId = playlistRepo.getSavedPlaylistId(),
            recentPlaylists = playlistRepo.getPlaylistHistory(),
            selectedSubjectCategoryId = subjectRepo.categories.firstOrNull()?.id,
            tracks = initialDefaultTracks,
            title = initialFirstTrack?.title ?: "Illuminati (Aavesham)",
            artist = initialFirstTrack?.artist ?: "Sushin Shyam, Dabzee",
            currentVideoId = initialFirstTrack?.videoId ?: "a3Ue-LN5B9U",
            thumbnailUrl = initialFirstTrack?.thumbnailUrl ?: "https://img.youtube.com/vi/a3Ue-LN5B9U/hqdefault.jpg",
            isLoading = false
        )
    )
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    init {
        // Load indexed songs and recently played songs from local memory on startup
        viewModelScope.launch(Dispatchers.IO) {
            if (initialDefaultTracks.isNotEmpty()) {
                indexedDb.insertOrUpdateBatch(initialDefaultTracks, _uiState.value.currentPlaylistId)
            }
            refreshLibraryFromDatabase()
        }

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
        if (tab == PlayerTab.SONGS) {
            refreshLibraryFromDatabase()
        }
    }

    fun setSongsFilterMode(mode: SongsFilterMode) {
        _uiState.update { it.copy(songsFilterMode = mode) }
    }

    fun updateSongsLogSearchQuery(query: String) {
        _uiState.update { it.copy(songsLogSearchQuery = query) }
        if (_uiState.value.songsFilterMode != SongsFilterMode.CURRENT_PLAYLIST) {
            viewModelScope.launch(Dispatchers.IO) {
                val filtered = indexedDb.getAllIndexedSongs(query)
                _uiState.update { it.copy(indexedSongs = filtered) }
            }
        }
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
        if (playlist.initialTracks.isNotEmpty()) {
            val firstTrack = playlist.initialTracks[0]
            _uiState.update {
                it.copy(
                    tracks = playlist.initialTracks,
                    currentIndex = 0,
                    title = firstTrack.title,
                    artist = firstTrack.artist,
                    currentVideoId = firstTrack.videoId,
                    thumbnailUrl = firstTrack.thumbnailUrl,
                    currentPlaylistId = playlist.id,
                    isLoading = false
                )
            }
            playerManager?.playVideoById(firstTrack.videoId)
            viewModelScope.launch(Dispatchers.IO) {
                indexedDb.insertOrUpdateBatch(playlist.initialTracks, playlist.id)
                refreshLibraryFromDatabase()
            }
        } else if (playlist.isSearchQuery) {
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
                    isLoading = false
                )
            }
            val vid = _uiState.value.currentVideoId
            if (vid.isNotBlank()) {
                playerManager?.playVideoById(vid)
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
            } else _uiState.value.thumbnailUrl

            val titleToUse = if (title.isNotBlank()) title else _uiState.value.title
            val resolvedLyrics = lyricsRepo.getSyncedLyrics(titleToUse, duration)

            // Save and record in Local Indexed Memory
            if (videoId.isNotBlank()) {
                val currentPlId = _uiState.value.currentPlaylistId
                viewModelScope.launch(Dispatchers.IO) {
                    indexedDb.recordSongPlayed(videoId, titleToUse, artist)
                    val updatedTrack = MusicTrack(
                        index = index,
                        videoId = videoId,
                        title = titleToUse,
                        artist = artist,
                        durationSeconds = duration
                    )
                    indexedDb.insertOrUpdateSong(updatedTrack, currentPlId)
                    refreshLibraryFromDatabase()
                }
            }

            _uiState.update { current ->
                val updatedTracks = if (current.tracks.isNotEmpty()) {
                    current.tracks.mapIndexed { idx, track ->
                        if (idx == index || (track.videoId.isNotEmpty() && track.videoId == videoId)) {
                            track.copy(
                                title = if (title.isNotBlank()) title else track.title,
                                artist = if (artist.isNotBlank()) artist else track.artist,
                                videoId = if (videoId.isNotBlank()) videoId else track.videoId,
                                durationSeconds = if (duration > 0f) duration else track.durationSeconds,
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
                    durationSeconds = if (duration > 0f) duration else current.durationSeconds,
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
            if (trackVideoIds.isEmpty()) return@launch

            // 1. Instant 0ms memory retrieval from Local Indexed DB
            val initialTracks = withContext(Dispatchers.IO) {
                trackVideoIds.mapIndexed { index, vid ->
                    val cached = indexedDb.getCachedSong(vid)
                    if (cached != null) {
                        cached.copy(index = index, isPlaying = (index == _uiState.value.currentIndex))
                    } else {
                        MusicTrack(
                            index = index,
                            videoId = vid,
                            title = "🎵 Track ${index + 1}",
                            artist = "YouTube Music",
                            isPlaying = (index == _uiState.value.currentIndex)
                        )
                    }
                }
            }

            _uiState.update {
                it.copy(
                    tracks = if (initialTracks.isNotEmpty()) initialTracks else it.tracks,
                    isLoading = false
                )
            }

            // 2. High-speed Multi-Chunk Parallel Loading (Async coroutines in chunks of 6)
            val currentPlId = _uiState.value.currentPlaylistId
            viewModelScope.launch(Dispatchers.IO) {
                val uncachedTracks = initialTracks.filter { it.title.startsWith("🎵 Track") }

                // Process in fast parallel chunks
                uncachedTracks.chunked(6).forEach { chunk ->
                    val deferredList = chunk.map { track ->
                        async {
                            val resolvedTitle = subjectRepo.fetchVideoTitle(track.videoId)
                            if (resolvedTitle.isNotBlank()) {
                                val resolvedTrack = track.copy(title = resolvedTitle)
                                indexedDb.insertOrUpdateSong(resolvedTrack, currentPlId)
                                resolvedTrack
                            } else {
                                null
                            }
                        }
                    }

                    val resolved = deferredList.awaitAll().filterNotNull()
                    if (resolved.isNotEmpty()) {
                        val resolvedMap = resolved.associateBy { it.videoId }
                        _uiState.update { current ->
                            val updated = current.tracks.map { t ->
                                resolvedMap[t.videoId] ?: t
                            }
                            current.copy(tracks = updated)
                        }
                    }
                }

                // Batch save loaded playlist to local database index
                indexedDb.insertOrUpdateBatch(_uiState.value.tracks, currentPlId)
                refreshLibraryFromDatabase()
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
        val nextIdx = (_uiState.value.currentIndex + 1)
        if (_uiState.value.tracks.isNotEmpty() && nextIdx < _uiState.value.tracks.size) {
            playTrack(nextIdx)
        } else {
            playerManager?.next()
        }
    }

    fun previousTrack() {
        val prevIdx = (_uiState.value.currentIndex - 1)
        if (prevIdx >= 0 && _uiState.value.tracks.isNotEmpty()) {
            playTrack(prevIdx)
        } else {
            playerManager?.previous()
        }
    }

    fun playTrack(index: Int) {
        if (index >= 0 && index < _uiState.value.tracks.size) {
            val track = _uiState.value.tracks[index]
            val resolvedLyrics = lyricsRepo.getSyncedLyrics(track.title, track.durationSeconds)
            val updatedTracks = _uiState.value.tracks.mapIndexed { idx, t ->
                t.copy(isPlaying = (idx == index))
            }
            _uiState.update {
                it.copy(
                    currentIndex = index,
                    title = track.title,
                    artist = track.artist,
                    currentVideoId = track.videoId,
                    thumbnailUrl = track.thumbnailUrl,
                    tracks = updatedTracks,
                    isPlaying = true,
                    isLoading = false,
                    lyrics = resolvedLyrics,
                    activeLyricIndex = 0
                )
            }
            playerManager?.playVideoById(track.videoId)
            viewModelScope.launch(Dispatchers.IO) {
                indexedDb.recordSongPlayed(track.videoId, track.title, track.artist)
                refreshLibraryFromDatabase()
            }
            updateForegroundService()
        } else {
            _uiState.update { it.copy(currentIndex = index, isPlaying = true) }
            playerManager?.playTrackAt(index)
        }
    }

    // Direct Instant Selection & Playback from Indexed Songs Log or History
    fun playSpecificTrack(track: MusicTrack) {
        val existingIndex = _uiState.value.tracks.indexOfFirst { it.videoId == track.videoId }
        if (existingIndex >= 0) {
            playTrack(existingIndex)
        } else {
            val resolvedLyrics = lyricsRepo.getSyncedLyrics(track.title, track.durationSeconds)
            val newTrack = track.copy(index = _uiState.value.tracks.size, isPlaying = true)
            val updatedTracks = _uiState.value.tracks.map { it.copy(isPlaying = false) } + newTrack
            _uiState.update {
                it.copy(
                    currentIndex = updatedTracks.size - 1,
                    title = track.title,
                    artist = track.artist,
                    currentVideoId = track.videoId,
                    thumbnailUrl = track.thumbnailUrl,
                    tracks = updatedTracks,
                    isPlaying = true,
                    isLoading = false,
                    lyrics = resolvedLyrics,
                    activeLyricIndex = 0
                )
            }
            playerManager?.playVideoById(track.videoId)
            viewModelScope.launch(Dispatchers.IO) {
                indexedDb.recordSongPlayed(track.videoId, track.title, track.artist)
                refreshLibraryFromDatabase()
            }
            updateForegroundService()
        }
        setTab(PlayerTab.PLAYER)
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

    fun refreshLibraryFromDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            val indexed = indexedDb.getAllIndexedSongs(_uiState.value.songsLogSearchQuery)
            val recent = indexedDb.getRecentlyPlayed(50)
            _uiState.update {
                it.copy(
                    indexedSongs = indexed,
                    recentSongs = recent
                )
            }
        }
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
