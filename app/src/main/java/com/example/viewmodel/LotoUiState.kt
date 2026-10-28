package com.example.viewmodel

import com.example.data.Draw
import com.example.util.GameStatistics
import com.example.util.GenerationResult
import com.example.util.LotoConfig

data class LotoUiState(
    val selectedGame: LotoConfig = LotoConfig.DEFAULT_GAME,
    val lockedNumbers: Set<Int> = emptySet(),
    val excludedNumbers: Set<Int> = emptySet(),
    val currentDraw: GenerationResult? = null,
    val statistics: GameStatistics = GameStatistics(),
    val recentDraws: List<Draw> = emptyList(),
    val allHistory: List<Draw> = emptyList(),
    val isGenerating: Boolean = false,
    val userNotification: String? = null,
    val showStatsDialog: Boolean = false,
    val showHistoryDialog: Boolean = false
) {
    val maxLockable: Int
        get() = selectedGame.drawCount

    fun isLocked(number: Int): Boolean = lockedNumbers.contains(number)
    fun isExcluded(number: Int): Boolean = excludedNumbers.contains(number)
    fun isGenerated(number: Int): Boolean = currentDraw?.numbers?.contains(number) == true
    fun isHot(number: Int): Boolean = statistics.isHot(number)
    fun isCold(number: Int): Boolean = statistics.isCold(number)
}
