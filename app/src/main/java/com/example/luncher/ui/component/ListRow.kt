package com.example.luncher.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.luncher.theme.OneUi

private val IconChipBlue = Color(0xFF3E91FF)

@Composable
fun ListRow(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconTint: Color = IconChipBlue,
    title: String,
    titleMaxLines: Int = 2,
    summary: String? = null,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    showDivider: Boolean = true
) {
    // OneUi tokens are already Dp values — use them directly.
    val rowMinHeight = OneUi.ListRowMinHeight
    val iconChipSize = OneUi.IconChipSize
    val horizontalPadding = 20.dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null && enabled) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            )
            .padding(horizontal = horizontalPadding)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = rowMinHeight),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(iconChipSize)
                )
                Spacer(modifier = Modifier.width(16.dp))
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = titleMaxLines,
                    modifier = Modifier.fillMaxWidth()
                )
                summary?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            trailing?.invoke()
        }

        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                    )
            )
        }
    }
}

@Composable
fun ListRowWithChevron(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconTint: Color = IconChipBlue,
    title: String,
    summary: String? = null,
    trailingValue: String? = null,
    onClick: (() -> Unit)? = null
) {
    ListRow(
        modifier = modifier,
        icon = icon,
        iconTint = iconTint,
        title = title,
        summary = summary,
        trailing = {
            if (trailingValue != null) {
                Text(
                    text = trailingValue,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = LocalContentColor.current,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        onClick = onClick,
        showDivider = true
    )
}
