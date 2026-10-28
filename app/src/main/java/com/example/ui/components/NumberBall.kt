package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ColdIce
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanAccentDark
import com.example.ui.theme.DarkSlateBackground
import com.example.ui.theme.DarkSlateOutline
import com.example.ui.theme.DarkSlateSurface
import com.example.ui.theme.ExcludedRed
import com.example.ui.theme.ExcludedRedDark
import com.example.ui.theme.HotOrange
import com.example.ui.theme.LockedGreen
import com.example.ui.theme.LockedGreenDark
import com.example.ui.theme.StarGold
import com.example.ui.theme.StarGoldDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

enum class BallStatus {
    NORMAL,
    LOCKED,      // Forced in draw (Green)
    EXCLUDED,    // Banned from draw (Red)
    GENERATED,   // Selected in current draw (Cyan gradient)
    EXTRA        // Bonus / Star ball (Gold)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NumberBall(
    number: Int,
    status: BallStatus = BallStatus.NORMAL,
    isHot: Boolean = false,
    isCold: Boolean = false,
    size: Dp = 46.dp,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = when (status) {
            BallStatus.GENERATED -> 1.08f
            BallStatus.LOCKED -> 1.05f
            BallStatus.EXCLUDED -> 0.92f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "ball_scale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = when (status) {
            BallStatus.LOCKED -> LockedGreen
            BallStatus.EXCLUDED -> ExcludedRedDark.copy(alpha = 0.6f)
            BallStatus.NORMAL -> DarkSlateSurface
            BallStatus.GENERATED -> CyanAccentDark
            BallStatus.EXTRA -> StarGoldDark
        },
        label = "ball_bg"
    )

    val borderColor = when (status) {
        BallStatus.LOCKED -> LockedGreenDark
        BallStatus.EXCLUDED -> ExcludedRed
        BallStatus.GENERATED -> CyanAccent
        BallStatus.EXTRA -> StarGold
        BallStatus.NORMAL -> when {
            isHot -> HotOrange
            isCold -> ColdIce
            else -> DarkSlateOutline.copy(alpha = 0.6f)
        }
    }

    val brush = when (status) {
        BallStatus.GENERATED -> Brush.radialGradient(
            colors = listOf(CyanAccent, CyanAccentDark),
            radius = 60f
        )
        BallStatus.LOCKED -> Brush.radialGradient(
            colors = listOf(Color(0xFF34D399), LockedGreen),
            radius = 60f
        )
        BallStatus.EXTRA -> Brush.radialGradient(
            colors = listOf(StarGold, StarGoldDark),
            radius = 60f
        )
        else -> null
    }

    Box(
        modifier = modifier
            .size(size)
            .scale(scale)
            .testTag("number_ball_$number"),
        contentAlignment = Alignment.Center
    ) {
        // Main ball surface
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .then(
                    if (brush != null) {
                        Modifier.background(brush)
                    } else {
                        Modifier.background(backgroundColor)
                    }
                )
                .border(
                    width = when (status) {
                        BallStatus.GENERATED -> 2.5.dp
                        BallStatus.LOCKED -> 2.dp
                        BallStatus.EXCLUDED -> 1.5.dp
                        else -> if (isHot || isCold) 1.5.dp else 1.dp
                    },
                    color = borderColor,
                    shape = CircleShape
                )
                .then(
                    if (onClick != null || onLongClick != null) {
                        Modifier.combinedClickable(
                            onClick = { onClick?.invoke() },
                            onLongClick = { onLongClick?.invoke() }
                        )
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            // Ball number text
            val textColor = when (status) {
                BallStatus.EXCLUDED -> TextMuted
                BallStatus.LOCKED -> DarkSlateBackground
                BallStatus.GENERATED -> DarkSlateBackground
                BallStatus.EXTRA -> DarkSlateBackground
                else -> TextPrimary
            }

            Text(
                text = number.toString(),
                color = textColor,
                fontSize = if (size < 36.dp) 11.sp else 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                textDecoration = if (status == BallStatus.EXCLUDED) TextDecoration.LineThrough else TextDecoration.None
            )
        }

        // Status badges for interactive grid
        if (size >= 40.dp) {
            when (status) {
                BallStatus.LOCKED -> {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 2.dp, y = (-2).dp)
                            .size(15.dp)
                            .clip(CircleShape)
                            .background(DarkSlateBackground)
                            .border(1.dp, LockedGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Blocat",
                            tint = LockedGreen,
                            modifier = Modifier.size(9.dp)
                        )
                    }
                }
                BallStatus.EXCLUDED -> {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 2.dp, y = (-2).dp)
                            .size(15.dp)
                            .clip(CircleShape)
                            .background(DarkSlateBackground)
                            .border(1.dp, ExcludedRed, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Exclus",
                            tint = ExcludedRed,
                            modifier = Modifier.size(9.dp)
                        )
                    }
                }
                BallStatus.NORMAL -> {
                    if (isHot) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 2.dp, y = (-2).dp)
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(DarkSlateBackground)
                                .border(1.dp, HotOrange, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Whatshot,
                                contentDescription = "Număr Fierbinte",
                                tint = HotOrange,
                                modifier = Modifier.size(9.dp)
                            )
                        }
                    } else if (isCold) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 2.dp, y = (-2).dp)
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(DarkSlateBackground)
                                .border(1.dp, ColdIce, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AcUnit,
                                contentDescription = "Număr Rece",
                                tint = ColdIce,
                                modifier = Modifier.size(9.dp)
                            )
                        }
                    }
                }
                else -> {}
            }
        }
    }
}
