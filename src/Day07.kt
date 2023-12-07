import java.lang.IllegalStateException

fun main() {
    fun part1(input: List<String>): Int {
        data class Card(val c: Char) : Comparable<Card> {
            val strength: Int = when(c) {
                '2' -> 2
                '3' -> 3
                '4' -> 4
                '5' -> 5
                '6' -> 6
                '7' -> 7
                '8' -> 8
                '9' -> 9
                'T' -> 10
                'J' -> 11
                'Q' -> 12
                'K' -> 13
                'A' -> 14
                else -> throw IllegalStateException("Invalid card: '$c'")
            }

            override fun compareTo(other: Card): Int {
                return strength.compareTo(other.strength)
            }
        }

        data class Hand(
                val cards: List<Card>,
                val bid: Int
        ) : Comparable<Hand> {
            val strength = calculateStrength()
            private fun calculateStrength(): Int {
                val cardSet = cards.toSet().associateWith { c -> cards.count { it.strength == c.strength } }
                val counts = cardSet.values
                if (cardSet.size == 1 && counts.first() == 5) {
                    // 5 of a kind
                    return 6
                }
                if (cardSet.size == 2 && counts.contains(4) && counts.contains(1)) {
                    // 4 of a kind
                    return 5
                }
                if (cardSet.size == 2 && counts.contains(3) && counts.contains(2)) {
                    // full house
                    return 4
                }
                if (cardSet.size == 3 && counts.contains(3) && counts.contains(1)) {
                    // 3 of a kind
                    return 3
                }
                if (cardSet.size == 3 && counts.contains(2) && counts.contains(1)) {
                    // 2 pair
                    return 2
                }
                if (cardSet.size == 4 && counts.contains(2) && counts.contains(1)) {
                    // 1 pair
                    return 1
                }
                if (cardSet.size == 5 && counts.all { it == 1 }) {
                    // high card
                    return 0
                }
                throw IllegalStateException("Failed to calculate hand strength: '$cards'")
            }

            override fun compareTo(other: Hand): Int {
                val handCompare = strength.compareTo(other.strength)
                if (handCompare != 0) {
                    return handCompare
                }

                cards.zip(other.cards).forEach { (x, y) ->
                    val cardCompare = x.strength.compareTo(y.strength)
                    if (cardCompare != 0) {
                        return cardCompare
                    }
                }

                throw IllegalStateException("Unexpected equal hands: $this, $other")
            }
        }

        return input.map { line->
            val parts = line.split(" ")
            val cards = parts[0].map { Card(it) }
            val bid = parts[1].toInt()
            Hand(cards, bid)
        }.sorted().mapIndexed { index, hand ->
            (index+1) * hand.bid
        }.sum()
    }

    fun part2(input: List<String>): Int {
        data class CardWithWilds(val c: Char) : Comparable<CardWithWilds> {
            val strength: Int = when(c) {
                '2' -> 2
                '3' -> 3
                '4' -> 4
                '5' -> 5
                '6' -> 6
                '7' -> 7
                '8' -> 8
                '9' -> 9
                'T' -> 10
                'J' -> 1
                'Q' -> 12
                'K' -> 13
                'A' -> 14
                else -> throw IllegalStateException("Invalid card: '$c'")
            }

            override fun compareTo(other: CardWithWilds): Int {
                return strength.compareTo(other.strength)
            }
        }

        val wildCard = CardWithWilds('J')

        data class HandWithWilds(
                val cards: List<CardWithWilds>,
                val bid: Int
        ) : Comparable<HandWithWilds> {

            val strength = calculateStrength()
            private fun calculateStrength(): Int {
                val cardSet = cards.toSet().associateWith { c -> cards.count { it.strength == c.strength } }
                val counts = cardSet.values

                val cardsWildsRemoved = cardSet.filterKeys { it != wildCard }
                val bestCard = cardsWildsRemoved.maxByOrNull { it.value }?.key
                if (bestCard != null && cards.contains(wildCard)) {
                    return HandWithWilds(cards.map { if (it == wildCard) bestCard else it }, bid).strength
                }

                if (cardSet.size == 1 && counts.first() == 5) {
                    // 5 of a kind
                    return 6
                }
                if (cardSet.size == 2 && counts.contains(4) && counts.contains(1)) {
                    // 4 of a kind
                    return 5
                }
                if (cardSet.size == 2 && counts.contains(3) && counts.contains(2)) {
                    // full house
                    return 4
                }
                if (cardSet.size == 3 && counts.contains(3) && counts.contains(1)) {
                    // 3 of a kind
                    return 3
                }
                if (cardSet.size == 3 && counts.contains(2) && counts.contains(1)) {
                    // 2 pair
                    return 2
                }
                if (cardSet.size == 4 && counts.contains(2) && counts.contains(1)) {
                    // 1 pair
                    return 1
                }
                if (cardSet.size == 5 && counts.all { it == 1 }) {
                    // high card
                    return 0
                }
                throw IllegalStateException("Failed to calculate hand strength: '$cards'")
            }

            override fun compareTo(other: HandWithWilds): Int {
                val handCompare = strength.compareTo(other.strength)
                if (handCompare != 0) {
                    return handCompare
                }

                cards.zip(other.cards).forEach { (x, y) ->
                    val cardCompare = x.strength.compareTo(y.strength)
                    if (cardCompare != 0) {
                        return cardCompare
                    }
                }

                throw IllegalStateException("Unexpected equal hands: $this, $other")
            }
        }

        return input.map { line->
            val parts = line.split(" ")
            val cards = parts[0].map { CardWithWilds(it) }
            val bid = parts[1].toInt()
            HandWithWilds(cards, bid)
        }.sorted().mapIndexed { index, hand ->
            (index+1) * hand.bid
        }.sum()
    }

    val testInput = readInput("Day07_test")
    checkEquals(6440, part1(testInput))
    checkEquals(5905, part2(testInput))

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))
}
