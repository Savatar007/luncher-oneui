package com.example.luncher

import android.app.SearchManager
import android.app.WallpaperManager
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.WindowCompat
import com.example.luncher.data.AppEntry
import com.example.luncher.data.AppRepository
import com.example.luncher.data.LauncherSettings
import com.example.luncher.data.PrefsStore
import com.example.luncher.theme.OneUiTheme
import com.example.luncher.ui.AppDrawer
import com.example.luncher.ui.HomeScreen
import com.example.luncher.ui.SettingsScreen
import com.example.luncher.ui.SineEasing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private enum class Screen { HOME, SETTINGS }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            OneUiTheme {
                LauncherRoot()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LauncherRoot() {
    val context = LocalContext.current

    var settings by remember { mutableStateOf(PrefsStore.load(context)) }
    var allApps by remember { mutableStateOf<List<AppEntry>>(emptyList()) }
    var dockApps by remember { mutableStateOf<List<AppEntry>>(emptyList()) }
    var wallpaper by remember { mutableStateOf<ImageBitmap?>(null) }

    var screen by remember { mutableStateOf(Screen.HOME) }
    var drawerOpen by remember { mutableStateOf(false) }
    var editMode by remember { mutableStateOf(false) }
    var sheetApp by remember { mutableStateOf<AppEntry?>(null) }
    var widgetsNotice by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            val apps = AppRepository.loadApps(context)
            val dockPkgs = AppRepository.defaultDockPackages(context)
            val dock = dockPkgs.mapNotNull { pkg -> apps.firstOrNull { it.packageName == pkg } }
            val wp = loadWallpaper(context)
            withContext(Dispatchers.Main) {
                allApps = apps
                dockApps = dock
                wallpaper = wp
            }
        }
    }

    fun updateSettings(next: LauncherSettings) {
        settings = next
        PrefsStore.save(context, next)
    }

    val visibleApps = remember(allApps, settings.hidden) {
        allApps.filter { it.packageName !in settings.hidden }
    }
    val homeApps = remember(visibleApps, settings.pinned, settings.homeColumns) {
        if (settings.pinned.isNotEmpty()) {
            settings.pinned.mapNotNull { pkg -> visibleApps.firstOrNull { it.packageName == pkg } }
        } else {
            visibleApps.take(settings.homeColumns * 4)
        }
    }

    BackHandler(enabled = drawerOpen) { drawerOpen = false }
    BackHandler(enabled = editMode) { editMode = false }
    BackHandler(enabled = screen == Screen.SETTINGS) { screen = Screen.HOME }

    if (screen == Screen.SETTINGS) {
        SettingsScreen(
            settings = settings,
            onChange = { updateSettings(it) },
            onBack = { screen = Screen.HOME }
        )
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HomeScreen(
            homeApps = homeApps,
            dockApps = dockApps,
            settings = settings,
            wallpaper = wallpaper,
            onOpenDrawer = { drawerOpen = true },
            onLaunch = { AppRepository.launch(context, it) },
            onLongPressApp = { sheetApp = it },
            onLongPressEmpty = { editMode = true },
            onSearchGoogle = {
                val intent = Intent(Intent.ACTION_WEB_SEARCH)
                    .putExtra(SearchManager.QUERY, "")
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    drawerOpen = true
                }
            },
            onSearchFinder = { drawerOpen = true }
        )

        AnimatedVisibility(
            visible = drawerOpen,
            enter = slideInVertically(animationSpec = tween(300, easing = SineEasing)) { it } +
                fadeIn(animationSpec = tween(300, easing = SineEasing)),
            exit = slideOutVertically(animationSpec = tween(280, easing = SineEasing)) { it } +
                fadeOut(animationSpec = tween(280, easing = SineEasing))
        ) {
            AppDrawer(
                apps = visibleApps,
                settings = settings,
                onLaunch = {
                    AppRepository.launch(context, it)
                    drawerOpen = false
                },
                onLongPressApp = { sheetApp = it },
                onOpenSettings = {
                    drawerOpen = false
                    screen = Screen.SETTINGS
                }
            )
        }

        if (editMode) {
            EditBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 18.dp),
                onWallpaper = {
                    editMode = false
                    val intent = Intent(Intent.ACTION_SET_WALLPAPER)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    try {
                        context.startActivity(
                            Intent.createChooser(intent, "Wallpaper and style")
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    } catch (e: Exception) {
                        Toast.makeText(context, "No wallpaper picker found", Toast.LENGTH_SHORT).show()
                    }
                },
                onWidgets = {
                    editMode = false
                    widgetsNotice = true
                },
                onSettings = {
                    editMode = false
                    screen = Screen.SETTINGS
                }
            )
        }
    }

    if (widgetsNotice) {
        AlertDialog(
            onDismissRequest = { widgetsNotice = false },
            title = { Text("Widgets") },
            text = { Text("Widgets arrive in Stage 2. They need an AppWidgetHost, which is Views-based, not pure Compose — it gets its own build step so it can be verified on its own.") },
            confirmButton = {
                TextButton(onClick = { widgetsNotice = false }) { Text("OK") }
            }
        )
    }

    sheetApp?.let { app ->
        val isPinned = settings.pinned.contains(app.packageName)
        ModalBottomSheet(
            onDismissRequest = { sheetApp = null },
            sheetState = rememberModalBottomSheetState(),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                Text(
                    text = app.label,
                    fontSize = 19.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                SheetRow(
                    text = if (isPinned) "Remove from Home screen" else "Add to Home screen",
                    onClick = {
                        val next = if (isPinned) {
                            settings.copy(pinned = settings.pinned - app.packageName)
                        } else {
                            settings.copy(pinned = settings.pinned + app.packageName)
                        }
                        updateSettings(next)
                        sheetApp = null
                    }
                )
                SheetRow(
                    text = "App info",
                    onClick = {
                        AppRepository.openAppInfo(context, app.packageName)
                        sheetApp = null
                    }
                )
                SheetRow(
                    text = "Hide app",
                    onClick = {
                        updateSettings(
                            settings.copy(
                                hidden = settings.hidden + app.packageName,
                                pinned = settings.pinned - app.packageName
                            )
                        )
                        sheetApp = null
                    }
                )
                Box(modifier = Modifier.padding(bottom = 28.dp))
            }
        }
    }
}

@Composable
private fun SheetRow(text: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = Color.Transparent
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(vertical = 14.dp)
        )
    }
}

@Composable
private fun EditBar(
    modifier: Modifier = Modifier,
    onWallpaper: () -> Unit,
    onWidgets: () -> Unit,
    onSettings: () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(32.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            EditBarButton(Icons.Filled.Image, "Wallpaper", onWallpaper)
            EditBarButton(Icons.Filled.Widgets, "Widgets", onWidgets)
            EditBarButton(Icons.Filled.Settings, "Settings", onSettings)
        }
    }
}

@Composable
private fun EditBarButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.onSurface
        )
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}

/** Current system wallpaper as a bitmap, scaled down. Null on any failure:
 *  the home screen then uses its fallback gradient. */
private fun loadWallpaper(context: android.content.Context): ImageBitmap? {
    return try {
        @Suppress("DEPRECATION")
        val drawable = WallpaperManager.getInstance(context).drawable ?: return null
        val width = 720
        val intrinsicW = drawable.intrinsicWidth
        val intrinsicH = drawable.intrinsicHeight
        val height = if (intrinsicW > 0 && intrinsicH > 0) {
            (width.toFloat() * intrinsicH / intrinsicW).toInt().coerceAtLeast(1)
        } else {
            1280
        }
        drawable.toBitmap(width, height).asImageBitmap()
    } catch (e: Exception) {
        null
    }
}
