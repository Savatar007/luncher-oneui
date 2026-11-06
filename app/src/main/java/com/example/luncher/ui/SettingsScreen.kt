package com.example.luncher.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luncher.data.HomeSearchMode
import com.example.luncher.data.LauncherSettings
import com.example.luncher.ui.component.GroupCard

private val Accent = Color(0xFF0381FE)
private val ControlBlue = Color(0xFF3E91FF)

/**
 * Home screen settings in the One UI style from the skill: floating back
 * button, large title, grouped cards (26-28dp, no borders), 24dp margins,
 * chunky pill switches with an outlined off state.
 */
@Composable
fun SettingsScreen(
    settings: LauncherSettings,
    onChange: (LauncherSettings) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Box(modifier = Modifier.padding(start = 16.dp, top = 10.dp)) {
            Surface(shape = CircleShape, shadowElevation = 6.dp, color = MaterialTheme.colorScheme.surface) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Home screen settings",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            item { SectionHeader("Home screen") }
            item {
                GroupCard {
                    Column {
                        SwitchRow(
                            title = "Show app labels",
                            summary = "Labels under icons on the Home screen",
                            checked = settings.showLabels,
                            onCheckedChange = { onChange(settings.copy(showLabels = it)) }
                        )
                        DividerInset()
                        SwitchRow(
                            title = "Floating dock",
                            summary = "Glass favourites tray, One UI 9 Home Up style",
                            checked = settings.floatingDock,
                            onCheckedChange = { onChange(settings.copy(floatingDock = it)) }
                        )
                        DividerInset()
                        ChoiceRow(
                            title = "Home screen grid",
                            options = listOf(4 to "4 columns", 5 to "5 columns"),
                            selected = settings.homeColumns,
                            onSelect = { onChange(settings.copy(homeColumns = it)) }
                        )
                    }
                }
            }

            item { SectionHeader("Show search on Home screen") }
            item {
                GroupCard {
                    Column {
                        RadioRow(
                            title = "Google and Finder",
                            summary = "Both pills, side by side (One UI 9)",
                            selected = settings.homeSearchMode == HomeSearchMode.BOTH,
                            onClick = { onChange(settings.copy(homeSearchMode = HomeSearchMode.BOTH)) }
                        )
                        DividerInset()
                        RadioRow(
                            title = "Google only",
                            selected = settings.homeSearchMode == HomeSearchMode.GOOGLE,
                            onClick = { onChange(settings.copy(homeSearchMode = HomeSearchMode.GOOGLE)) }
                        )
                        DividerInset()
                        RadioRow(
                            title = "Finder only",
                            summary = "Search apps on this phone",
                            selected = settings.homeSearchMode == HomeSearchMode.FINDER,
                            onClick = { onChange(settings.copy(homeSearchMode = HomeSearchMode.FINDER)) }
                        )
                        DividerInset()
                        RadioRow(
                            title = "Off",
                            selected = settings.homeSearchMode == HomeSearchMode.OFF,
                            onClick = { onChange(settings.copy(homeSearchMode = HomeSearchMode.OFF)) }
                        )
                    }
                }
            }

            item { SectionHeader("Apps screen") }
            item {
                GroupCard {
                    Column {
                        ChoiceRow(
                            title = "Apps screen grid",
                            options = listOf(4 to "4 columns", 5 to "5 columns"),
                            selected = settings.drawerColumns,
                            onSelect = { onChange(settings.copy(drawerColumns = it)) }
                        )
                        if (settings.hidden.isNotEmpty()) {
                            DividerInset()
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Hidden apps",
                                        fontSize = 17.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${settings.hidden.size} hidden",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                TextButton(onClick = { onChange(settings.copy(hidden = emptySet())) }) {
                                    Text("Unhide all", color = Accent)
                                }
                            }
                        }
                    }
                }
            }

            item { SectionHeader("About") }
            item {
                GroupCard {
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                        Text(
                            text = "Luncher",
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "One UI 9 style. Lawnchair feature plan, Stage 1.",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = Accent,
        modifier = Modifier.padding(start = 8.dp, top = 12.dp, bottom = 2.dp)
    )
}

@Composable
private fun DividerInset() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp)
            .height(1.dp)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
    )
}

@Composable
private fun SwitchRow(
    title: String,
    summary: String?,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 17.sp, color = MaterialTheme.colorScheme.onSurface)
            if (summary != null) {
                Text(
                    text = summary,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = ControlBlue,
                checkedThumbColor = Color.White,
                checkedBorderColor = ControlBlue,
                uncheckedTrackColor = Color.Transparent,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedBorderColor = MaterialTheme.colorScheme.outline
            )
        )
    }
}

@Composable
private fun RadioRow(
    title: String,
    summary: String? = null,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 17.sp, color = MaterialTheme.colorScheme.onSurface)
            if (summary != null) {
                Text(
                    text = summary,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        // One UI radio: filled blue dot when selected, outlined ring when not.
        Surface(
            modifier = Modifier.size(22.dp),
            shape = CircleShape,
            color = if (selected) Accent else Color.Transparent,
            border = if (selected) null else androidx.compose.foundation.BorderStroke(
                2.dp, MaterialTheme.colorScheme.outline
            )
        ) {
            if (selected) {
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color.White, CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
private fun ChoiceRow(
    title: String,
    options: List<Pair<Int, String>>,
    selected: Int,
    onSelect: (Int) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
        Text(text = title, fontSize = 17.sp, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { (value, label) ->
                val isSelected = value == selected
                Surface(
                    shape = CircleShape,
                    color = if (isSelected) Accent else MaterialTheme.colorScheme.surfaceVariant,
                    onClick = { onSelect(value) }
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
    }
}
