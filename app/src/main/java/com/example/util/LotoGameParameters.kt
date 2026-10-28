package com.example.util

/**
 * Utility class that defines the exact game parameters and helper functions
 * for all supported lottery games:
 * 1. 6/49 (România 6/49)
 * 2. Euromilion (5/50 + 2 Stele din 12)
 * 3. UK 6/59 (Marea Britanie)
 * 4. Joker (România 6/49 + 1 Joker din 20)
 * 5. 5/40 (România 5/40)
 */
object LotoGameParameters {

    /**
     * 6 din 49: 49 numere totale, se extrag 6.
     * Suma optimă statistică: 120 - 180.
     */
    val GAME_6_49: LotoConfig = LotoConfig(
        id = "649",
        name = "6 din 49",
        totalNumbers = 49,
        drawCount = 6,
        extraNumbersCount = 0,
        extraNumbersTotal = 0,
        extraLabel = "",
        description = "România 6/49 (se extrag 6 numere din 49)",
        targetMinSum = 120,
        targetMaxSum = 180
    )

    /**
     * Euromilion: 50 numere totale, se extrag 5 numere principale + 2 Stele din 12.
     * Suma optimă statistică: 100 - 155.
     */
    val GAME_EUROMILION: LotoConfig = LotoConfig(
        id = "euromillions",
        name = "Euromilion",
        totalNumbers = 50,
        drawCount = 5,
        extraNumbersCount = 2,
        extraNumbersTotal = 12,
        extraLabel = "Stele",
        description = "Euromilion (se extrag 5 numere din 50 + 2 Stele din 12)",
        targetMinSum = 100,
        targetMaxSum = 155
    )

    /**
     * UK 6/59: 59 numere totale, se extrag 6.
     * Suma optimă statistică: 145 - 215.
     */
    val GAME_UK_6_59: LotoConfig = LotoConfig(
        id = "uk659",
        name = "UK 6/59",
        totalNumbers = 59,
        drawCount = 6,
        extraNumbersCount = 0,
        extraNumbersTotal = 0,
        extraLabel = "",
        description = "Marea Britanie UK Lotto (se extrag 6 numere din 59)",
        targetMinSum = 145,
        targetMaxSum = 215
    )

    /**
     * Joker / România 6/49+1: 49 numere principale, se extrag 6 + 1 număr bonus Joker (1..20).
     * Suma optimă statistică: 120 - 180.
     */
    val GAME_JOKER: LotoConfig = LotoConfig(
        id = "ro649plus1",
        name = "România 6/49+1",
        totalNumbers = 49,
        drawCount = 6,
        extraNumbersCount = 1,
        extraNumbersTotal = 20,
        extraLabel = "Joker",
        description = "România 6/49 + 1 Număr Joker (1..20)",
        targetMinSum = 120,
        targetMaxSum = 180
    )

    /**
     * România 5/40: 40 numere totale, se extrag 5.
     * Suma optimă statistică: 80 - 125.
     */
    val GAME_5_40: LotoConfig = LotoConfig(
        id = "ro540",
        name = "România 5/40",
        totalNumbers = 40,
        drawCount = 5,
        extraNumbersCount = 0,
        extraNumbersTotal = 0,
        extraLabel = "",
        description = "România 5/40 (se extrag 5 numere din 40)",
        targetMinSum = 80,
        targetMaxSum = 125
    )

    /**
     * Complete list of all supported lottery game configurations.
     */
    val ALL_GAMES: List<LotoConfig> = listOf(
        GAME_6_49,
        GAME_EUROMILION,
        GAME_UK_6_59,
        GAME_JOKER,
        GAME_5_40
    )

    /**
     * Default game used upon application launch.
     */
    val DEFAULT_GAME: LotoConfig = GAME_6_49

    /**
     * Finds a game configuration by its identifier or alias.
     */
    fun getGameById(id: String): LotoConfig? {
        val normalized = id.trim().lowercase()
        return ALL_GAMES.find {
            it.id.equals(normalized, ignoreCase = true) ||
            (normalized in listOf("joker", "ro_joker") && it.id == "ro649plus1") ||
            (normalized in listOf("540", "ro_540") && it.id == "ro540") ||
            (normalized in listOf("649", "ro_649") && it.id == "649") ||
            (normalized in listOf("euromilion", "euromill") && it.id == "euromillions")
        }
    }

    /**
     * Validates whether a list of numbers meets the game's constraints.
     */
    fun validateMainNumbers(game: LotoConfig, numbers: List<Int>): Boolean {
        if (numbers.size != game.drawCount) return false
        val distinct = numbers.distinct()
        if (distinct.size != numbers.size) return false
        return numbers.all { it in 1..game.totalNumbers }
    }

    /**
     * Validates extra / bonus numbers (such as Euromilion stars or Joker).
     */
    fun validateExtraNumbers(game: LotoConfig, extras: List<Int>): Boolean {
        if (game.extraNumbersCount == 0) return extras.isEmpty()
        if (extras.size != game.extraNumbersCount) return false
        val distinct = extras.distinct()
        if (distinct.size != extras.size) return false
        return extras.all { it in 1..game.extraNumbersTotal }
    }
}

/**
 * Typealias for convenience so both LotoGameUtil and LotoGameParameters can be used interchangeably.
 */
typealias LotoGameUtil = LotoGameParameters
