package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanAccentDark
import com.example.ui.theme.DarkSlateBackground
import com.example.ui.theme.DarkSlateOutline
import com.example.ui.theme.DarkSlateSurface
import com.example.ui.theme.StarGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.LotoConfig

@Composable
fun GameSelector(
    selectedGame: LotoConfig,
    onSelectGame: (LotoConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .testTag("game_selector_row"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(LotoConfig.ALL_GAMES, key = { it.id }) { game ->
            val isSelected = game.id == selectedGame.id

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .testTag("game_chip_${game.id}")
                    .then(
                        if (isSelected) {
                            Modifier
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            DarkSlateSurface,
                                            CyanAccentDark.copy(alpha = 0.35f)
                                        )
                                    )
                                )
                                .border(1.5.dp, CyanAccent, RoundedCornerShape(14.dp))
                        } else {
                            Modifier
                                .background(DarkSlateSurface.copy(alpha = 0.7f))
                                .border(1.dp, DarkSlateOutline.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        }
                    )
                    .clickable { onSelectGame(game) }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.Casino,
                        contentDescription = null,
                        tint = if (isSelected) CyanAccent else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = game.name,
                            color = if (isSelected) CyanAccent else TextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        val subtitle = if (game.extraNumbersCount > 0) {
                            "${game.drawCount}/${game.totalNumbers} + ${game.extraNumbersCount} ${game.extraLabel}"
                        } else {
                            "${game.drawCount} din ${game.totalNumbers}"
                        }

                        Text(
                            text = subtitle,
                            color = if (isSelected) TextSecondary else TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
