package com.example.musicplayer.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.musicplayer.R
import com.example.musicplayer.ui.theme.AccentGreen

@Composable
fun SpinningAlbumArt(
    thumbnailUrl: String,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    var rotationAngle by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isPlaying) {
        var lastTime = withFrameMillis { it }
        while (isPlaying) {
            val currentTime = withFrameMillis { it }
            val delta = (currentTime - lastTime) / 1000f
            // 360 degrees per 10 seconds = 36 deg/sec
            rotationAngle = (rotationAngle + delta * 36f) % 360f
            lastTime = currentTime
        }
    }

    Box(
        modifier = modifier
            .size(190.dp)
            .shadow(20.dp, shape = CircleShape, ambientColor = Color.Black, spotColor = AccentGreen)
            .clip(CircleShape)
            .background(Color(0xFF1E1E1E))
            .border(3.dp, Brush.radialGradient(listOf(Color(0x661DB954), Color(0x22FFFFFF), Color.Transparent)), CircleShape)
            .rotate(rotationAngle)
            .testTag("album_art_circle"),
        contentAlignment = Alignment.Center
    ) {
        if (thumbnailUrl.isNotBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(thumbnailUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = stringResource(R.string.album_art_desc),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        } else {
            // Vinyl disc styling
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = this.center
                val radius = size.minDimension / 2f
                // Vinyl grooves
                drawCircle(Color(0xFF181818), radius = radius)
                for (r in listOf(0.85f, 0.70f, 0.55f, 0.40f)) {
                    drawCircle(
                        color = Color(0x33FFFFFF),
                        radius = radius * r,
                        style = Stroke(width = 1.5f)
                    )
                }
            }
        }

        // Center vinyl disc hole / accent badge
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFF121212))
                .border(2.dp, AccentGreen, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = null,
                tint = AccentGreen,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
