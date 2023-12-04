import kotlin.math.pow

fun main() {
    fun determineMatches(input: List<String>): Map<Int, Int> {
        return input.associate { line ->
            val cardParts = line.split(":").map { it.trim() }

            val gameNumber = cardParts[0].replace("Card ", "").trim().toInt()

            val rawNums = cardParts[1].split("|").map { it.trim() }
            val winningNums = rawNums[0].split(" ").filter { it.isNotBlank() }.toSet()
            val yourNums = rawNums[1].split(" ").filter { it.isNotBlank() }.toSet()

            val matchingNums = yourNums.intersect(winningNums)
            val numMatches = matchingNums.size
            Pair(gameNumber, numMatches)
        }
    }

    fun part1(input: List<String>): Int {
        return determineMatches(input).values.sumOf { numMatches ->
            val score = 2f.pow(numMatches - 1).toInt()
            score
        }
    }

    fun calculateTotalCards(cardNum: Int, cardMatches: Map<Int, Int>, scoreCache: MutableMap<Int, Int>) : Int {
        if (scoreCache.containsKey(cardNum)) {
            return scoreCache[cardNum]!!
        }

        val numToCopy = cardMatches[cardNum]!!
        val score = (cardNum+1 .. cardNum+numToCopy).sumOf { calculateTotalCards(it, cardMatches, scoreCache) } + numToCopy
        scoreCache.putIfAbsent(cardNum, score)
        return score
    }

    fun part2(input: List<String>): Int {
        val cards = determineMatches(input)
        val scoreCache = mutableMapOf<Int, Int>()
        val totalCards = cards.keys.sumOf { cardNum ->
            calculateTotalCards(cardNum, cards, scoreCache)
        } + cards.size
//        println(scoreCache)
//        println(totalCards)
        return totalCards
    }

    val testInput = readInput("Day04_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 30)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}
