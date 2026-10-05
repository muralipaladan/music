package com.example.musicplayer.data.model

data class MusicTrack(
    val index: Int,
    val videoId: String = "",
    val title: String = "🎵 Track ${index + 1}",
    val artist: String = "YouTube Music",
    val durationSeconds: Float = 0f,
    val isPlaying: Boolean = false
) {
    val thumbnailUrl: String
        get() = if (videoId.isNotEmpty()) {
            "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
        } else {
            ""
        }
}
