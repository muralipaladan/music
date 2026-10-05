package com.example.musicplayer.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.musicplayer.R
import com.example.musicplayer.data.model.SubjectPlaylist
import com.example.musicplayer.ui.theme.AccentGreen

@Composable
fun SearchTab(
    searchQuery: String,
    results: List<SubjectPlaylist>,
    onQueryChange: (String) -> Unit,
    onPlayPlaylist: (SubjectPlaylist) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onQueryChange,
            placeholder = { Text(stringResource(R.string.search_placeholder), color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AccentGreen) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("search_input")
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.testTag("search_results_list")
        ) {
            items(results) { playlist ->
                SubjectPlaylistCard(
                    playlist = playlist,
                    onPlay = { onPlayPlaylist(playlist) }
                )
            }
        }
    }
}
