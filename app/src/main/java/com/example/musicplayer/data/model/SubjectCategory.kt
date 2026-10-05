package com.example.musicplayer.data.model

data class SubjectPlaylist(
    val id: String,
    val title: String,
    val description: String,
    val subjectId: String,
    val songCount: Int = 25,
    val thumbnailUrl: String = "",
    val isSearchQuery: Boolean = false,
    val searchQuery: String = ""
)

data class SubjectCategory(
    val id: String,
    val titleMalayalam: String,
    val titleEnglish: String,
    val iconName: String,
    val playlists: List<SubjectPlaylist>
)
