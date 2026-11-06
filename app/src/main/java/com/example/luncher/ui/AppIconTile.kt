package com.example.luncher.ui

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luncher.data.AppEntry

/** One UI motion: sine in-out, from the one-ui-design skill tokens. */
val SineEasing = CubicBezierEasing(0.33f, 0f, 0.67f, 1f)

/** Samsung-style icon: squircle clip (~30% corner), label underneath. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppIconTile(
    app: AppEntry,
    showLabel: Boolean,
    labelColor: Color,
    modifier: Modifier = Modifier,
    iconSize: Dp = 56.dp,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Column(
        modifier = modifier.combinedClickable(onClick = onClick, onLongClick = onLongClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            bitmap = app.icon,
            contentDescription = app.label,
            modifier = Modifier
                .size(iconSize)
                .clip(RoundedCornerShape(percent = 30))
        )
        if (showLabel) {
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = app.label,
                color = labelColor,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                // Shadow only over wallpaper (white labels). Over the drawer's
                // solid background it would just blur the text.
                style = if (labelColor == Color.White) {
                    TextStyle(
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.45f),
                            offset = Offset(0f, 2f),
                            blurRadius = 6f
                        )
                    )
                } else {
                    TextStyle()
                }
            )
        }
    }
}
