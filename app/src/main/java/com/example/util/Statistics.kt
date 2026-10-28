package com.example.util

import com.example.data.Draw

data class GameStatistics(
    val totalDrawsAnalyzed: Int = 0,
    val frequencyMap: Map<Int, Int> = emptyMap(),
    val hotNumbers: List<Int> = emptyList(),
    val coldNumbers: List<Int> = emptyList(),
    val extraFrequencyMap: Map<Int, Int> = emptyMap(),
    val hotExtraNumbers: List<Int> = emptyList(),
    val coldExtraNumbers: List<Int> = emptyList()
) {
    fun isHot(number: Int): Boolean = hotNumbers.contains(number)
    fun isCold(number: Int): Boolean = coldNumbers.contains(number)
    fun getFrequency(number: Int): Int = frequencyMap[number] ?: 0
}

object StatisticsCalculator {

    /**
     * Calculates frequencies, hot (top 5), and cold (bottom 5) numbers
     * based on up to 50 recent draws.
     */
    fun calculate(
        draws: List<Draw>,
        totalNumbers: Int,
        extraTotal: Int = 0
    ): GameStatistics {
        if (draws.isEmpty()) {
            return GameStatistics()
        }

        val analyzedDraws = draws.take(50)
        val mainFrequencies = (1..totalNumbers).associateWith { 0 }.toMutableMap()
        val extraFrequencies = if (extraTotal > 0) {
            (1..extraTotal).associateWith { 0 }.toMutableMap()
        } else {
            mutableMapOf()
        }

        for (draw in analyzedDraws) {
            for (num in draw.parsedNumbers()) {
                if (num in 1..totalNumbers) {
                    mainFrequencies[num] = (mainFrequencies[num] ?: 0) + 1
                }
            }
            if (extraTotal > 0) {
                for (extra in draw.parsedExtraNumbers()) {
                    if (extra in 1..extraTotal) {
                        extraFrequencies[extra] = (extraFrequencies[extra] ?: 0) + 1
                    }
                }
            }
        }

        // Hot: top 5 by count (descending)
        val sortedDesc = mainFrequencies.entries.sortedWith(
            compareByDescending<Map.Entry<Int, Int>> { it.value }
                .thenBy { it.key }
        )
        val hot = sortedDesc.take(5).map { it.key }

        // Cold: bottom 5 by count (ascending)
        val sortedAsc = mainFrequencies.entries.sortedWith(
            compareBy<Map.Entry<Int, Int>> { it.value }
                .thenBy { it.key }
        )
        val cold = sortedAsc.take(5).map { it.key }

        val hotExtra = if (extraTotal > 0) {
            extraFrequencies.entries
                .sortedWith(compareByDescending<Map.Entry<Int, Int>> { it.value }.thenBy { it.key })
                .take(3)
                .map { it.key }
        } else emptyList()

        val coldExtra = if (extraTotal > 0) {
            extraFrequencies.entries
                .sortedWith(compareBy<Map.Entry<Int, Int>> { it.value }.thenBy { it.key })
                .take(3)
                .map { it.key }
        } else emptyList()

        return GameStatistics(
            totalDrawsAnalyzed = analyzedDraws.size,
            frequencyMap = mainFrequencies,
            hotNumbers = hot,
            coldNumbers = cold,
            extraFrequencyMap = extraFrequencies,
            hotExtraNumbers = hotExtra,
            coldExtraNumbers = coldExtra
        )
    }
}
