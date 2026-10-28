package com.example.util

import kotlin.math.abs
import kotlin.random.Random

data class GenerationResult(
    val numbers: List<Int>,
    val extraNumbers: List<Int> = emptyList(),
    val sum: Int,
    val oddCount: Int,
    val evenCount: Int,
    val isLuckyAi: Boolean
)

object NumberGenerator {

    /**
     * Standard random generator:
     * - Strictly includes all [lockedNumbers]
     * - Strictly excludes all [excludedNumbers]
     * - Randomly picks remaining numbers to reach [drawCount]
     */
    fun generateRandom(
        config: LotoConfig,
        lockedNumbers: Set<Int>,
        excludedNumbers: Set<Int>
    ): GenerationResult {
        val total = config.totalNumbers
        val drawCount = config.drawCount

        // Clean valid locked numbers
        val guaranteed = lockedNumbers.filter { it in 1..total && !excludedNumbers.contains(it) }.take(drawCount).toMutableSet()

        // Available pool for remaining slots
        val candidatePool = (1..total)
            .filter { !guaranteed.contains(it) && !excludedNumbers.contains(it) }
            .toMutableList()

        candidatePool.shuffle()

        val needed = drawCount - guaranteed.size
        val pickedRemaining = candidatePool.take(needed)

        val finalNumbers = (guaranteed + pickedRemaining).sorted()

        val extraNumbers = generateExtras(config)

        val sum = finalNumbers.sum()
        val oddCount = finalNumbers.count { it % 2 != 0 }
        val evenCount = finalNumbers.count { it % 2 == 0 }

        return GenerationResult(
            numbers = finalNumbers,
            extraNumbers = extraNumbers,
            sum = sum,
            oddCount = oddCount,
            evenCount = evenCount,
            isLuckyAi = false
        )
    }

    /**
     * Lucky AI generator:
     * - Respects locked and excluded numbers.
     * - Blends Hot and Cold numbers based on statistical frequency.
     * - Satisfies sum criteria (targetMinSum..targetMaxSum) and balanced odd/even ratio.
     */
    fun generateLuckyAi(
        config: LotoConfig,
        lockedNumbers: Set<Int>,
        excludedNumbers: Set<Int>,
        statistics: GameStatistics
    ): GenerationResult {
        val total = config.totalNumbers
        val drawCount = config.drawCount

        val guaranteed = lockedNumbers.filter { it in 1..total && !excludedNumbers.contains(it) }.take(drawCount).toSet()
        val needed = drawCount - guaranteed.size

        if (needed <= 0) {
            val finalNumbers = guaranteed.toList().sorted()
            return GenerationResult(
                numbers = finalNumbers,
                extraNumbers = generateExtras(config),
                sum = finalNumbers.sum(),
                oddCount = finalNumbers.count { it % 2 != 0 },
                evenCount = finalNumbers.count { it % 2 == 0 },
                isLuckyAi = true
            )
        }

        // Available numbers (excluding guaranteed and excluded)
        val availablePool = (1..total)
            .filter { !guaranteed.contains(it) && !excludedNumbers.contains(it) }

        val hotAvailable = statistics.hotNumbers.filter { availablePool.contains(it) }
        val coldAvailable = statistics.coldNumbers.filter { availablePool.contains(it) }
        val neutralAvailable = availablePool.filter { !hotAvailable.contains(it) && !coldAvailable.contains(it) }

        var bestCandidate: List<Int>? = null
        var bestScore = Int.MAX_VALUE

        val targetEven = drawCount / 2
        val targetOdd = drawCount - targetEven
        val minSum = config.targetMinSum
        val maxSum = config.targetMaxSum

        // Try up to 250 iterations to find an optimal combination
        for (attempt in 0..250) {
            val candidateSelection = mutableSetOf<Int>()

            // Strategy: Mix 1-2 Hot numbers + remaining from Cold and Neutral (skewed to cold)
            val hotPickCount = if (hotAvailable.isNotEmpty() && needed >= 3) {
                Random.nextInt(1, minOf(3, hotAvailable.size + 1, needed))
            } else 0

            val shuffledHot = hotAvailable.shuffled()
            candidateSelection.addAll(shuffledHot.take(hotPickCount))

            // Cold numbers given higher priority
            val remainingNeeded = needed - candidateSelection.size
            val coldPickCount = if (coldAvailable.isNotEmpty() && remainingNeeded > 0) {
                Random.nextInt(1, minOf(coldAvailable.size + 1, remainingNeeded + 1))
            } else 0

            val shuffledCold = coldAvailable.shuffled()
            candidateSelection.addAll(shuffledCold.take(coldPickCount))

            // Fill remaining with neutral or any available
            val stillNeeded = needed - candidateSelection.size
            if (stillNeeded > 0) {
                val remainingPool = (neutralAvailable + hotAvailable + coldAvailable)
                    .filter { !candidateSelection.contains(it) }
                    .shuffled()
                candidateSelection.addAll(remainingPool.take(stillNeeded))
            }

            // Combine guaranteed + candidate
            val candidateFull = (guaranteed + candidateSelection).toList().sorted()
            if (candidateFull.size != drawCount) continue

            val sum = candidateFull.sum()
            val odd = candidateFull.count { it % 2 != 0 }
            val even = candidateFull.count { it % 2 == 0 }

            val sumPenalty = when {
                sum < minSum -> (minSum - sum) * 2
                sum > maxSum -> (sum - maxSum) * 2
                else -> 0
            }

            val parityPenalty = (abs(odd - targetOdd) + abs(even - targetEven)) * 10

            // Penalty for consecutive sequences of length >= 3
            var sequencePenalty = 0
            for (i in 0 until candidateFull.size - 2) {
                if (candidateFull[i + 1] == candidateFull[i] + 1 && candidateFull[i + 2] == candidateFull[i] + 2) {
                    sequencePenalty += 30
                }
            }

            val totalScore = sumPenalty + parityPenalty + sequencePenalty

            if (totalScore < bestScore) {
                bestScore = totalScore
                bestCandidate = candidateFull
                if (totalScore == 0) break // Perfect match found!
            }
        }

        val finalNumbers = bestCandidate ?: run {
            val fallback = availablePool.shuffled().take(needed)
            (guaranteed + fallback).sorted()
        }

        val extraNumbers = generateExtras(config)
        val sum = finalNumbers.sum()
        val oddCount = finalNumbers.count { it % 2 != 0 }
        val evenCount = finalNumbers.count { it % 2 == 0 }

        return GenerationResult(
            numbers = finalNumbers,
            extraNumbers = extraNumbers,
            sum = sum,
            oddCount = oddCount,
            evenCount = evenCount,
            isLuckyAi = true
        )
    }

    private fun generateExtras(config: LotoConfig): List<Int> {
        if (config.extraNumbersCount <= 0 || config.extraNumbersTotal <= 0) {
            return emptyList()
        }
        val pool = (1..config.extraNumbersTotal).toMutableList()
        pool.shuffle()
        return pool.take(config.extraNumbersCount).sorted()
    }
}
