package com.example.luncher.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luncher.data.AppEntry
import com.example.luncher.data.HomeSearchMode
import com.example.luncher.data.LauncherSettings
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val FallbackWallpaper = Brush.verticalGradient(
    listOf(Color(0xFF2B3A55), Color(0xFF101623), Color(0xFF000000))
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    homeApps: List<AppEntry>,
    dockApps: List<AppEntry>,
    settings: LauncherSettings,
    wallpaper: ImageBitmap?,
    onOpenDrawer: () -> Unit,
    onLaunch: (AppEntry) -> Unit,
    onLongPressApp: (AppEntry) -> Unit,
    onLongPressEmpty: () -> Unit,
    onSearchGoogle: () -> Unit,
    onSearchFinder: () -> Unit
) {
    val dragTotal = remember { mutableStateOf(0f) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Wallpaper layer: also the long-press target for edit mode.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .combinedClickable(onClick = {}, onLongClick = onLongPressEmpty)
        ) {
            if (wallpaper != null) {
                Image(
                    bitmap = wallpaper,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.18f))
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(FallbackWallpaper)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragStart = { dragTotal.value = 0f },
                        onVerticalDrag = { _, dragAmount -> dragTotal.value += dragAmount },
                        onDragEnd = { if (dragTotal.value < -90f) onOpenDrawer() }
                    )
                }
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(18.dp))
            ClockHeader()
            Spacer(modifier = Modifier.height(20.dp))

            if (settings.homeSearchMode != HomeSearchMode.OFF) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (settings.homeSearchMode == HomeSearchMode.GOOGLE ||
                        settings.homeSearchMode == HomeSearchMode.BOTH
                    ) {
                        HomeSearchPill(
                            text = "Google",
                            modifier = Modifier.weight(1f),
                            onClick = onSearchGoogle
                        )
                    }
                    if (settings.homeSearchMode == HomeSearchMode.FINDER ||
                        settings.homeSearchMode == HomeSearchMode.BOTH
                    ) {
                        HomeSearchPill(
                            text = "Finder",
                            modifier = Modifier.weight(1f),
                            onClick = onSearchFinder
                        )
                    }
                }
                Spacer(modifier = Modifier.height(22.dp))
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(settings.homeColumns),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                userScrollEnabled = false,
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(homeApps, key = { it.key }) { app ->
                    AppIconTile(
                        app = app,
                        showLabel = settings.showLabels,
                        labelColor = Color.White,
                        onClick = { onLaunch(app) },
                        onLongClick = { onLongPressApp(app) }
                    )
                }
            }

            // Page indicator: one page in Stage 1, dots are the Samsung cue.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .background(Color.White, CircleShape)
                )
                Spacer(modifier = Modifier.size(6.dp))
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .background(Color.White.copy(alpha = 0.4f), CircleShape)
                )
            }

            Dock(dockApps = dockApps, floating = settings.floatingDock, onLaunch = onLaunch, onLongPressApp = onLongPressApp)
            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}

@Composable
private fun ClockHeader() {
    var now by remember { mutableStateOf(LocalDateTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = LocalDateTime.now()
            delay(1000)
        }
    }
    Column {
        Text(
            text = now.format(DateTimeFormatter.ofPattern("h:mm")),
            color = Color.White,
            fontSize = 54.sp,
            fontWeight = FontWeight.Light
        )
        Text(
            text = now.format(DateTimeFormatter.ofPattern("EEE, MMMM d")),
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 15.sp
        )
    }
}

@Composable
private fun HomeSearchPill(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.height(46.dp),
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.92f),
        shadowElevation = 6.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                tint = Color(0xFF5F6368),
                modifier = Modifier.size(20.dp)
            )
            Text(text = text, color = Color(0xFF5F6368), fontSize = 15.sp)
        }
    }
}

@Composable
private fun Dock(
    dockApps: List<AppEntry>,
    floating: Boolean,
    onLaunch: (AppEntry) -> Unit,
    onLongPressApp: (AppEntry) -> Unit
) {
    val row: @Composable () -> Unit = {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            dockApps.forEach { app ->
                AppIconTile(
                    app = app,
                    showLabel = false,
                    labelColor = Color.White,
                    iconSize = 58.dp,
                    onClick = { onLaunch(app) },
                    onLongClick = { onLongPressApp(app) }
                )
            }
        }
    }
    if (floating) {
        // One UI 9 Home Up style: floating glass favourites tray.
        // True backdrop blur is a Stage 2 item (needs a blur library);
        // Stage 1 uses translucency + border + soft shadow.
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(36.dp),
            color = Color.White.copy(alpha = 0.20f),
            shadowElevation = 10.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.28f))
        ) { row() }
    } else {
        row()
    }
}
