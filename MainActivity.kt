package com.vivinand.kaelmusic

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay

private val Night = Color(0xFF090A14)
private val Panel = Color(0xFF151625)
private val Purple = Color(0xFFAC68F5)
private val Pink = Color(0xFFEB65C5)
private val Muted = Color(0xFFA9A8BD)

data class Track(val uri: Uri, val title: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { KaelTheme { KaelMusicApp() } }
    }
}

@Composable
private fun KaelTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Purple,
            secondary = Pink,
            background = Night,
            surface = Panel,
            onBackground = Color.White,
            onSurface = Color.White
        ),
        content = content
    )
}

@Composable
private fun KaelMusicApp() {
    val context = LocalContext.current
    val tracks = remember { mutableStateListOf<Track>() }
    var currentIndex by remember { mutableIntStateOf(-1) }
    var playing by remember { mutableStateOf(false) }
    var player by remember { mutableStateOf<MediaPlayer?>(null) }
    var duration by remember { mutableIntStateOf(0) }
    var position by remember { mutableIntStateOf(0) }
    var message by remember { mutableStateOf("Import music from your phone to get started.") }

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        uris.forEach { uri ->
            val name = uri.lastPathSegment?.substringAfterLast('/')?.substringAfterLast(':')
                ?.takeIf { it.isNotBlank() } ?: "Local audio"
            if (tracks.none { it.uri == uri }) tracks.add(Track(uri, name))
        }
        if (uris.isNotEmpty()) message = "${uris.size} file(s) added to your library."
    }

    fun stopPlayer() {
        try { player?.stop() } catch (_: Exception) {}
        try { player?.release() } catch (_: Exception) {}
        player = null
        playing = false
    }

    fun playTrack(index: Int) {
        if (index !in tracks.indices) return
        stopPlayer()
        currentIndex = index
        try {
            val newPlayer = MediaPlayer()
            newPlayer.setDataSource(context, tracks[index].uri)
            newPlayer.setOnPreparedListener { mp ->
                duration = mp.duration.coerceAtLeast(0)
                position = 0
                mp.start()
                playing = true
            }
            newPlayer.setOnCompletionListener {
                if (currentIndex + 1 < tracks.size) playTrack(currentIndex + 1)
                else playing = false
            }
            newPlayer.setOnErrorListener { _, _, _ ->
                message = "This audio file could not be played. Try another file."
                playing = false
                true
            }
            player = newPlayer
            newPlayer.prepareAsync()
            message = "Loading ${tracks[index].title}…"
        } catch (e: Exception) {
            message = "Could not open this file. Please choose another audio file."
            playing = false
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            try { player?.release() } catch (_: Exception) {}
        }
    }

    LaunchedEffect(playing, player) {
        while (playing) {
            try {
                position = player?.currentPosition ?: position
                duration = player?.duration ?: duration
            } catch (_: Exception) {}
            delay(500)
        }
    }

    val current = tracks.getOrNull(currentIndex)

    Column(
        modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF151027), Night, Night))
        ).padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(26.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(15.dp))
                    .background(Brush.linearGradient(listOf(Purple, Pink))),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color.White, modifier = Modifier.size(30.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text("KAEL MUSIC", fontSize = 22.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                Text("Feel Every Beat", color = Pink, fontSize = 12.sp, letterSpacing = 1.4.sp)
            }
        }
        Spacer(Modifier.height(26.dp))
        Text("Your music. Your mood.", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text("A fresh space for every language and every beat.", color = Muted, fontSize = 14.sp)
        Spacer(Modifier.height(20.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            FeatureChip("Local music", Icons.Default.FolderOpen, Modifier.weight(1f))
            FeatureChip("Your library", Icons.Default.LibraryMusic, Modifier.weight(1f))
        }
        Spacer(Modifier.height(18.dp))

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.weight(1f)) {
                Text("My Library", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("${tracks.size} audio file(s)", color = Muted, fontSize = 12.sp)
            }
            Button(
                onClick = { picker.launch(arrayOf("audio/*")) },
                colors = ButtonDefaults.buttonColors(containerColor = Purple),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("Add songs")
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(message, color = Muted, fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))

        if (tracks.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth().clip(RoundedCornerShape(24.dp))
                    .background(Panel).padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.GraphicEq, contentDescription = null, tint = Purple, modifier = Modifier.size(56.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("Your next favourite starts here", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text("Tap “Add songs” to choose audio files stored on your phone.", color = Muted, fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                itemsIndexed(tracks) { index, track ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                            .background(if (index == currentIndex) Color(0xFF29203F) else Panel)
                            .clickable { playTrack(index) }.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.size(44.dp).clip(RoundedCornerShape(12.dp))
                            .background(Brush.linearGradient(listOf(Purple.copy(alpha = .85f), Pink.copy(alpha = .75f)))),
                            contentAlignment = Alignment.Center
                        ) { Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color.White) }
                        Spacer(Modifier.width(12.dp))
                        Text(track.title, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Medium)
                        IconButton(onClick = { playTrack(index) }) {
                            Icon(if (index == currentIndex && playing) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play ${track.title}", tint = Purple)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(Panel).padding(16.dp)) {
            Text("NOW PLAYING", color = Purple, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
            Spacer(Modifier.height(4.dp))
            Text(current?.title ?: "Nothing playing yet", fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Slider(
                value = if (duration > 0) position.coerceIn(0, duration).toFloat() else 0f,
                onValueChange = { position = it.toInt() },
                onValueChangeFinished = { try { player?.seekTo(position) } catch (_: Exception) {} },
                valueRange = 0f..(duration.coerceAtLeast(1).toFloat()),
                enabled = current != null,
                colors = SliderDefaults.colors(thumbColor = Pink, activeTrackColor = Purple)
            )
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = { if (currentIndex > 0) playTrack(currentIndex - 1) }, enabled = currentIndex > 0) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", tint = Color.White)
                }
                IconButton(
                    onClick = {
                        val mp = player
                        if (mp == null && tracks.isNotEmpty()) playTrack(if (currentIndex < 0) 0 else currentIndex)
                        else if (mp != null) {
                            try {
                                if (mp.isPlaying) { mp.pause(); playing = false }
                                else { mp.start(); playing = true }
                            } catch (_: Exception) {}
                        }
                    },
                    modifier = Modifier.size(54.dp).clip(CircleShape).background(Brush.linearGradient(listOf(Purple, Pink)))
                ) {
                    Icon(if (playing) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = "Play or pause", tint = Color.White, modifier = Modifier.size(30.dp))
                }
                IconButton(onClick = { if (currentIndex + 1 < tracks.size) playTrack(currentIndex + 1) }, enabled = currentIndex + 1 < tracks.size) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = Color.White)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Text("Developed & Designed by Vivinand", color = Muted, fontSize = 11.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun FeatureChip(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.clip(RoundedCornerShape(14.dp)).background(Panel).padding(horizontal = 12.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = null, tint = Purple, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(7.dp))
        Text(label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
