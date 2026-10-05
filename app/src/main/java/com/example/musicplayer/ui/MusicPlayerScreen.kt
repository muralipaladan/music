package com.example.musicplayer.ui

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.musicplayer.R
import com.example.musicplayer.player.YouTubePlayerManager
import com.example.musicplayer.ui.components.*
import com.example.musicplayer.ui.theme.*
import com.example.musicplayer.ui.viewmodel.MusicPlayerViewModel
import com.example.musicplayer.ui.viewmodel.PlayerTab

@Composable
fun MusicPlayerScreen(
    viewModel: MusicPlayerViewModel,
    playerManager: YouTubePlayerManager,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { errorMsg ->
            snackbarHostState.showSnackbar(errorMsg)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF0F2027)) {
                listOf(PlayerTab.PLAYER, PlayerTab.SUBJECTS, PlayerTab.SONGS, PlayerTab.SEARCH).forEach { tab ->
                    NavigationBarItem(
                        selected = uiState.currentTab == tab,
                        onClick = { viewModel.setTab(tab) },
                        label = { Text(getTabLabel(tab)) },
                        icon = { Icon(getTabIcon(tab), contentDescription = null) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AccentGreen,
                            selectedTextColor = AccentGreen,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        },
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(DarkBackgroundStart, DarkBackgroundMiddle, DarkBackgroundEnd)
                    )
                )
                .padding(innerPadding)
        ) {
            AndroidView(
                factory = { ctx ->
                    val webView = playerManager.initialize(uiState.currentPlaylistId)
                    FrameLayout(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(1, 1)
                        if (webView.parent == null) addView(webView)
                    }
                },
                modifier = Modifier.size(1.dp).testTag("youtube_engine_webview")
            )

            when (uiState.currentTab) {
                PlayerTab.PLAYER -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        SpinningAlbumArt(uiState.thumbnailUrl, uiState.isPlaying)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(uiState.title, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                        Text(uiState.artist, color = TextSecondary, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        PlayerControls(
                            isPlaying = uiState.isPlaying,
                            currentTimeSeconds = uiState.currentTimeSeconds,
                            durationSeconds = uiState.durationSeconds,
                            onTogglePlay = { viewModel.togglePlay() },
                            onPrevious = { viewModel.previousTrack() },
                            onNext = { viewModel.nextTrack() },
                            onSeek = { sec -> viewModel.seekTo(sec) },
                            onToggleLyrics = { viewModel.toggleLyricsPanel() },
                            isLyricsActive = uiState.showLyricsPanel
                        )
                    }
                }
                PlayerTab.SUBJECTS -> {
                    SubjectCategoryTab(
                        categories = viewModel.categories,
                        selectedCategoryId = uiState.selectedSubjectCategoryId,
                        onSelectCategory = { viewModel.selectSubjectCategory(it) },
                        onPlayPlaylist = { viewModel.playSubjectPlaylist(it) }
                    )
                }
                PlayerTab.SONGS -> {
                    SongsTab(
                        currentPlaylistTracks = uiState.tracks,
                        indexedSongs = uiState.indexedSongs,
                        recentSongs = uiState.recentSongs,
                        filterMode = uiState.songsFilterMode,
                        searchQuery = uiState.songsLogSearchQuery,
                        currentVideoId = uiState.currentVideoId,
                        isPlaying = uiState.isPlaying,
                        onFilterChange = { viewModel.setSongsFilterMode(it) },
                        onSearchChange = { viewModel.updateSongsLogSearchQuery(it) },
                        onTrackClick = { viewModel.playSpecificTrack(it) }
                    )
                }
                PlayerTab.SEARCH -> {
                    SearchTab(
                        searchQuery = uiState.searchQuery,
                        results = uiState.searchResults,
                        onQueryChange = { viewModel.updateSearchQuery(it) },
                        onPlayPlaylist = { viewModel.playSubjectPlaylist(it) }
                    )
                }
            }

            // Slide-up Synced Lyrics Overlay Panel
            SyncedLyricsPanel(
                isVisible = uiState.showLyricsPanel,
                lyrics = uiState.lyrics,
                activeIndex = uiState.activeLyricIndex,
                onSeekTo = { sec -> viewModel.seekTo(sec) },
                onClose = { viewModel.toggleLyricsPanel() },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun getTabLabel(tab: PlayerTab) = when(tab) {
    PlayerTab.PLAYER -> stringResource(R.string.tab_player)
    PlayerTab.SUBJECTS -> stringResource(R.string.tab_subjects)
    PlayerTab.SONGS -> stringResource(R.string.tab_playlist)
    PlayerTab.SEARCH -> stringResource(R.string.tab_search)
}

@Composable
fun getTabIcon(tab: PlayerTab) = when(tab) {
    PlayerTab.PLAYER -> Icons.Default.MusicNote
    PlayerTab.SUBJECTS -> Icons.Default.Info
    PlayerTab.SONGS -> Icons.Default.Album
    PlayerTab.SEARCH -> Icons.Default.Search
}
