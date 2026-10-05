package com.example.musicplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.musicplayer.player.YouTubePlayerManager
import com.example.musicplayer.ui.MusicPlayerScreen
import com.example.musicplayer.ui.theme.YouTubeMusicPlayerTheme
import com.example.musicplayer.ui.viewmodel.MusicPlayerViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MusicPlayerViewModel by viewModels()
    private lateinit var playerManager: YouTubePlayerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        playerManager = YouTubePlayerManager(this, viewModel)
        viewModel.setPlayerManager(playerManager)

        setContent {
            YouTubeMusicPlayerTheme {
                MusicPlayerScreen(
                    viewModel = viewModel,
                    playerManager = playerManager
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playerManager.release()
    }
}
