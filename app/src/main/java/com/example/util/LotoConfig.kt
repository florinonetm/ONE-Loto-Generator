package com.example.util

/**
 * Clasă de date 'GameConfig' pentru configurarea jocurilor de loto.
 *
 * @property id Identificator unic al jocului (ex: "649", "euromillions", "uk659", "ro649plus1", "ro540")
 * @property name Numele afișat în interfață (ex: "6 din 49", "Euromilion", "UK 6/59", "România 6/49+1", "România 5/40")
 * @property totalNumbers Numărul total de bile principale din urnă (1..totalNumbers)
 * @property drawCount Numărul de bile extrase
 * @property extraNumbersCount Numărul de bile bonus / stele (ex: 2 la Euromilion, 1 la Joker)
 * @property extraNumbersTotal Urna numerelor bonus (ex: 12 la Euromilion, 20 la Joker)
 * @property extraLabel Etichetă pentru bilele bonus ("Stele", "Joker")
 * @property description Descrierea formatului jocului
 * @property targetMinSum Suma minimă recomandată statistic pentru Lucky AI
 * @property targetMaxSum Suma maximă recomandată statistic pentru Lucky AI
 */
data class GameConfig(
    val id: String,
    val name: String,
    val totalNumbers: Int,
    val drawCount: Int,
    val extraNumbersCount: Int = 0,
    val extraNumbersTotal: Int = 0,
    val extraLabel: String = "",
    val description: String = "",
    val targetMinSum: Int = 120,
    val targetMaxSum: Int = 180
) {
    val hasExtraNumbers: Boolean
        get() = extraNumbersCount > 0 && extraNumbersTotal > 0

    companion object {
        val ALL_GAMES: List<GameConfig>
            get() = LotoConfigurations.ALL_GAMES

        val DEFAULT_GAME: GameConfig
            get() = LotoConfigurations.DEFAULT_GAME

        fun findById(id: String): GameConfig = LotoConfigurations.findById(id)
    }
}

/**
 * Typealias pentru compatibilitate completă cu referințele 'LotoConfig'.
 */
typealias LotoConfig = GameConfig

/**
 * Listă și definiții ale jocurilor de loto suportate:
 * - 6 din 49 (România 6/49)
 * - Euromilion (5 din 50 + 2 Stele din 12)
 * - UK 6/59 (Marea Britanie)
 * - Joker (România 6/49 + 1 Joker din 20)
 * - România 5/40
 */
object LotoConfigurations {

    val GAME_6_49 = GameConfig(
        id = "649",
        name = "6 din 49",
        totalNumbers = 49,
        drawCount = 6,
        description = "România 6/49 (se extrag 6 numere din 49)",
        targetMinSum = 120,
        targetMaxSum = 180
    )

    val GAME_EUROMILION = GameConfig(
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

    val GAME_UK_6_59 = GameConfig(
        id = "uk659",
        name = "UK 6/59",
        totalNumbers = 59,
        drawCount = 6,
        description = "Marea Britanie UK Lotto (se extrag 6 numere din 59)",
        targetMinSum = 145,
        targetMaxSum = 215
    )

    val GAME_JOKER = GameConfig(
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

    val GAME_5_40 = GameConfig(
        id = "ro540",
        name = "România 5/40",
        totalNumbers = 40,
        drawCount = 5,
        description = "România 5/40 (se extrag 5 numere din 40)",
        targetMinSum = 80,
        targetMaxSum = 125
    )

    /**
     * Lista completă de configurări pentru toate jocurile.
     */
    val ALL_GAMES: List<GameConfig> = listOf(
        GAME_6_49,
        GAME_EUROMILION,
        GAME_UK_6_59,
        GAME_JOKER,
        GAME_5_40
    )

    val DEFAULT_GAME: GameConfig = GAME_6_49

    fun findById(id: String): GameConfig {
        val normalized = id.trim().lowercase()
        return ALL_GAMES.find {
            it.id.equals(normalized, ignoreCase = true) ||
            (normalized in listOf("joker", "ro_joker") && it.id == "ro649plus1") ||
            (normalized in listOf("540", "ro_540") && it.id == "ro540") ||
            (normalized in listOf("649", "ro_649") && it.id == "649") ||
            (normalized in listOf("euromilion", "euromill") && it.id == "euromillions")
        } ?: DEFAULT_GAME
    }
}

/**
 * Listă globală de configurări pentru acces direct.
 */
val gameConfigurations: List<GameConfig> = LotoConfigurations.ALL_GAMES
