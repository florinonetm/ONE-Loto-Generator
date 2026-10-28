package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BallStatus
import com.example.ui.components.FullHistoryDialog
import com.example.ui.components.GameSelector
import com.example.ui.components.NumberBall
import com.example.ui.components.RecentDrawsSection
import com.example.ui.components.StatisticsDialog
import com.example.ui.theme.ColdIce
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanAccentDark
import com.example.ui.theme.DarkSlateBackground
import com.example.ui.theme.DarkSlateOutline
import com.example.ui.theme.DarkSlateSurface
import com.example.ui.theme.DarkSlateSurfaceVariant
import com.example.ui.theme.ExcludedRed
import com.example.ui.theme.HotOrange
import com.example.ui.theme.LockedGreen
import com.example.ui.theme.StarGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.LotoUiState
import com.example.viewmodel.LotoViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GeneratorScreen(
    viewModel: LotoViewModel,
    uiState: LotoUiState,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    // Handle user notifications
    LaunchedEffect(uiState.userNotification) {
        uiState.userNotification?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissNotification()
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("generator_screen"),
        containerColor = DarkSlateBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopBrandingBar(
                onOpenStats = { viewModel.setShowStatsDialog(true) },
                onOpenHistory = { viewModel.setShowHistoryDialog(true) }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Game Selector Chips
            item {
                Spacer(modifier = Modifier.height(6.dp))
                GameSelector(
                    selectedGame = uiState.selectedGame,
                    onSelectGame = { game -> viewModel.selectGame(game) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Current Generated Draw Display (Hero Section)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CurrentDrawHeroCard(
                        currentDraw = uiState.currentDraw,
                        isGenerating = uiState.isGenerating,
                        gameName = uiState.selectedGame.name,
                        extraLabel = uiState.selectedGame.extraLabel
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Primary Action Buttons
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ActionButtonsBar(
                        isGenerating = uiState.isGenerating,
                        onGenerateRandom = { viewModel.generateRandom() },
                        onGenerateLuckyAi = { viewModel.generateLuckyAi() },
                        onReset = { viewModel.resetGridAndDraw() }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Interactive Number Grid Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    NumberGridCard(
                        totalNumbers = uiState.selectedGame.totalNumbers,
                        lockedNumbers = uiState.lockedNumbers,
                        excludedNumbers = uiState.excludedNumbers,
                        generatedNumbers = uiState.currentDraw?.numbers ?: emptyList(),
                        maxLockable = uiState.maxLockable,
                        isHot = { num -> uiState.isHot(num) },
                        isCold = { num -> uiState.isCold(num) },
                        onToggleLock = { num -> viewModel.toggleLock(num) },
                        onToggleExclude = { num -> viewModel.toggleExclude(num) }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Recent 3 Saved Draws List
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    RecentDrawsSection(
                        recentDraws = uiState.recentDraws,
                        onViewAllHistory = { viewModel.setShowHistoryDialog(true) },
                        onDeleteDraw = { id -> viewModel.deleteHistoryItem(id) },
                        modifier = Modifier.widthIn(max = 600.dp)
                    )
                }
            }

            // Footer Branding
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "ONE™ Media Gateway • onestudiohub.ro/loto.html",
                        color = TextMuted,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Generator statistic algoritmic cu Room DB și Dark Theme",
                        color = TextMuted.copy(alpha = 0.6f),
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    // Full History Modal Dialog
    if (uiState.showHistoryDialog) {
        FullHistoryDialog(
            gameName = uiState.selectedGame.name,
            history = uiState.allHistory,
            onDismiss = { viewModel.setShowHistoryDialog(false) },
            onDeleteDraw = { id -> viewModel.deleteHistoryItem(id) },
            onClearAll = { viewModel.clearAllHistoryForCurrentGame() }
        )
    }

    // Statistics Modal Dialog
    if (uiState.showStatsDialog) {
        StatisticsDialog(
            game = uiState.selectedGame,
            statistics = uiState.statistics,
            onDismiss = { viewModel.setShowStatsDialog(false) }
        )
    }
}

@Composable
private fun TopBrandingBar(
    onOpenStats: () -> Unit,
    onOpenHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkSlateBackground)
            .border(
                width = 1.dp,
                color = DarkSlateOutline.copy(alpha = 0.35f),
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "ONE™",
                        color = CyanAccent,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Loto Generator",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Algoritm inteligent Lucky AI & Statistici",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onOpenStats,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(DarkSlateSurface)
                        .testTag("stats_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.QueryStats,
                        contentDescription = "Statistici",
                        tint = CyanAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = onOpenHistory,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(DarkSlateSurface)
                        .testTag("history_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Istoric",
                        tint = CyanAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CurrentDrawHeroCard(
    currentDraw: com.example.util.GenerationResult?,
    isGenerating: Boolean,
    gameName: String,
    extraLabel: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 600.dp)
            .testTag("current_draw_hero_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(
                colors = listOf(CyanAccent.copy(alpha = 0.7f), DarkSlateOutline.copy(alpha = 0.3f))
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Extragere Curentă • $gameName",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                if (currentDraw != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (currentDraw.isLuckyAi) CyanAccentDark.copy(alpha = 0.4f)
                                else DarkSlateSurfaceVariant
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (currentDraw.isLuckyAi) "🍀 Lucky AI" else "🎲 Aleatoriu",
                            color = if (currentDraw.isLuckyAi) CyanAccent else TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (isGenerating) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = CyanAccent,
                            modifier = Modifier.size(32.dp),
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Se calculează combinația optimă...",
                            color = CyanAccent,
                            fontSize = 12.sp
                        )
                    }
                }
            } else if (currentDraw == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSlateBackground.copy(alpha = 0.6f))
                        .border(1.dp, DarkSlateOutline.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Casino,
                            contentDescription = null,
                            tint = CyanAccent.copy(alpha = 0.6f),
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Alege numere sau apasă pe [Generează Aleatoriu] ori [Lucky AI]",
                            color = TextMuted,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // Generated Lottery Balls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    currentDraw.numbers.forEach { num ->
                        NumberBall(
                            number = num,
                            status = BallStatus.GENERATED,
                            size = 44.dp,
                            modifier = Modifier.padding(horizontal = 3.dp)
                        )
                    }

                    if (currentDraw.extraNumbers.isNotEmpty()) {
                        Text(
                            text = "+",
                            color = StarGold,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )

                        currentDraw.extraNumbers.forEach { extraNum ->
                            NumberBall(
                                number = extraNum,
                                status = BallStatus.EXTRA,
                                size = 42.dp,
                                modifier = Modifier.padding(horizontal = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stats summary badges
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatBadge(label = "Sumă Numere", value = "${currentDraw.sum}")
                    StatBadge(label = "Paritate (P/I)", value = "${currentDraw.evenCount}/${currentDraw.oddCount}")
                    if (currentDraw.extraNumbers.isNotEmpty()) {
                        StatBadge(label = extraLabel, value = currentDraw.extraNumbers.joinToString(", "))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBadge(
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = TextMuted, fontSize = 10.sp)
        Text(text = value, color = CyanAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ActionButtonsBar(
    isGenerating: Boolean,
    onGenerateRandom: () -> Unit,
    onGenerateLuckyAi: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 600.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // [🎲 Generează Aleatoriu]
            Button(
                onClick = onGenerateRandom,
                enabled = !isGenerating,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("generate_random_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkSlateSurfaceVariant,
                    contentColor = TextPrimary
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Casino,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Generează Aleatoriu",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // [🍀 Lucky AI]
            Button(
                onClick = onGenerateLuckyAi,
                enabled = !isGenerating,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("generate_lucky_ai_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyanAccent,
                    contentColor = DarkSlateBackground
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = DarkSlateBackground,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Lucky AI",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        // [Resetează]
        OutlinedButton(
            onClick = onReset,
            enabled = !isGenerating,
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .testTag("reset_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = Brush.horizontalGradient(listOf(DarkSlateOutline, DarkSlateOutline))
            )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Resetează Blocaje și Grilă",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NumberGridCard(
    totalNumbers: Int,
    lockedNumbers: Set<Int>,
    excludedNumbers: Set<Int>,
    generatedNumbers: List<Int>,
    maxLockable: Int,
    isHot: (Int) -> Boolean,
    isCold: (Int) -> Boolean,
    onToggleLock: (Int) -> Unit,
    onToggleExclude: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 600.dp)
            .testTag("number_grid_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSlateSurface.copy(alpha = 0.95f)),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(DarkSlateOutline.copy(alpha = 0.5f))
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with instructions and counts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Grilă Numere Interactive",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Click: Blochează (verde) • Long-press: Exclude (roșu)",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(LockedGreen.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Blocate: ${lockedNumbers.size}/$maxLockable",
                            color = LockedGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (excludedNumbers.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ExcludedRed.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Excluse: ${excludedNumbers.size}",
                                color = ExcludedRed,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Legend indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendIndicator(color = LockedGreen, label = "Blocat")
                LegendIndicator(color = ExcludedRed, label = "Exclus")
                LegendIndicator(color = HotOrange, label = "Fierbinte")
                LegendIndicator(color = ColdIce, label = "Rece")
                LegendIndicator(color = CyanAccent, label = "Generat")
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Balls Flow Grid
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                maxItemsInEachRow = 7
            ) {
                for (num in 1..totalNumbers) {
                    val status = when {
                        lockedNumbers.contains(num) -> BallStatus.LOCKED
                        excludedNumbers.contains(num) -> BallStatus.EXCLUDED
                        generatedNumbers.contains(num) -> BallStatus.GENERATED
                        else -> BallStatus.NORMAL
                    }

                    Box(modifier = Modifier.padding(horizontal = 4.dp)) {
                        NumberBall(
                            number = num,
                            status = status,
                            isHot = isHot(num),
                            isCold = isCold(num),
                            size = 42.dp,
                            onClick = { onToggleLock(num) },
                            onLongClick = { onToggleExclude(num) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendIndicator(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 10.sp
        )
    }
}
