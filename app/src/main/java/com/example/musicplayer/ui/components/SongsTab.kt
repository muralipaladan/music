package com.example.musicplayer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.musicplayer.data.model.MusicTrack
import com.example.musicplayer.ui.theme.AccentGreen
import com.example.musicplayer.ui.theme.AccentGreenBright
import com.example.musicplayer.ui.theme.TextPrimary
import com.example.musicplayer.ui.theme.TextSecondary
import com.example.musicplayer.ui.viewmodel.SongsFilterMode

@Composable
fun SongsTab(
    currentPlaylistTracks: List<MusicTrack>,
    indexedSongs: List<MusicTrack>,
    recentSongs: List<MusicTrack>,
    filterMode: SongsFilterMode,
    searchQuery: String,
    currentVideoId: String,
    isPlaying: Boolean,
    onFilterChange: (SongsFilterMode) -> Unit,
    onSearchChange: (String) -> Unit,
    onTrackClick: (MusicTrack) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Tab Filter Chips (Current Playlist, Local Indexed DB, History)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = filterMode == SongsFilterMode.CURRENT_PLAYLIST,
                onClick = { onFilterChange(SongsFilterMode.CURRENT_PLAYLIST) },
                label = { Text("പ്ലേലിസ്റ്റ് (${currentPlaylistTracks.size})", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.MusicNote, contentDescription = null, modifier = Modifier.size(16.dp)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AccentGreen,
                    selectedLabelColor = Color.White
                ),
                modifier = Modifier.weight(1f).testTag("filter_current_playlist")
            )

            FilterChip(
                selected = filterMode == SongsFilterMode.ALL_INDEXED,
                onClick = { onFilterChange(SongsFilterMode.ALL_INDEXED) },
                label = { Text("ലോക്കൽ DB (${indexedSongs.size})", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.LibraryMusic, contentDescription = null, modifier = Modifier.size(16.dp)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AccentGreen,
                    selectedLabelColor = Color.White
                ),
                modifier = Modifier.weight(1f).testTag("filter_indexed_library")
            )

            FilterChip(
                selected = filterMode == SongsFilterMode.RECENTLY_PLAYED,
                onClick = { onFilterChange(SongsFilterMode.RECENTLY_PLAYED) },
                label = { Text("ഹിസ്റ്ററി (${recentSongs.size})", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AccentGreen,
                    selectedLabelColor = Color.White
                ),
                modifier = Modifier.weight(1f).testTag("filter_recently_played")
            )
        }

        // Search in local Indexed Memory when looking through saved library or history
        if (filterMode != SongsFilterMode.CURRENT_PLAYLIST) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("ലോക്കൽ മെമ്മറിയിൽ പാട്ടുകൾ തിരയുക...", fontSize = 13.sp, color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AccentGreen) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentGreen,
                    unfocusedBorderColor = Color(0x33FFFFFF),
                    focusedContainerColor = Color(0x22FFFFFF),
                    unfocusedContainerColor = Color(0x11FFFFFF)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .testTag("songs_log_search_input")
            )
        }

        // Active list based on selected filter
        val displayTracks = when (filterMode) {
            SongsFilterMode.CURRENT_PLAYLIST -> currentPlaylistTracks
            SongsFilterMode.ALL_INDEXED -> indexedSongs
            SongsFilterMode.RECENTLY_PLAYED -> recentSongs
        }

        if (displayTracks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = when (filterMode) {
                        SongsFilterMode.CURRENT_PLAYLIST -> "പ്ലേലിസ്റ്റിൽ പാട്ടുകൾ ഇല്ല..."
                        SongsFilterMode.ALL_INDEXED -> "ലോക്കൽ മെമ്മറിയിൽ പാട്ടുകൾ സേവ് ആയിട്ടില്ല. പ്ലേലിസ്റ്റ് ലോഡ് ചെയ്യുമ്പോൾ തനിയെ ഇവിടെ സൂക്ഷിക്കപ്പെടും!"
                        SongsFilterMode.RECENTLY_PLAYED -> "അവസാനം കേട്ട പാട്ടുകൾ ഇവിടെ കാണാം..."
                    },
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(displayTracks) { index, track ->
                    val isCurrent = track.videoId.isNotEmpty() && track.videoId == currentVideoId

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCurrent) Color(0x331DB954) else Color(0x1AFFFFFF)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTrackClick(track) }
                            .testTag("song_item_$index")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Thumbnail
                            if (track.thumbnailUrl.isNotEmpty()) {
                                AsyncImage(
                                    model = track.thumbnailUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0x33FFFFFF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MusicNote,
                                        contentDescription = null,
                                        tint = AccentGreenBright
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = track.title,
                                    color = if (isCurrent) AccentGreenBright else TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = track.artist,
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            // Play indicator button
                            IconButton(
                                onClick = { onTrackClick(track) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = if (isCurrent) AccentGreenBright else Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
