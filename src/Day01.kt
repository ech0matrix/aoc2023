fun main() {
    fun part1(input: List<String>): Int {
        return input.sumOf { line ->
            val first = line.first { it.isDigit() }.toString()
            val last = line.last { it.isDigit() }.toString()
            (first + last).toInt()
        }
    }

    fun part2(input: List<String>): Int {
        return input.sumOf { line ->
            val firstDigit = line.indexOfFirst { it.isDigit() }
            val firstNum = mapOf(
                    Pair(firstDigit, if (firstDigit >= 0) line[firstDigit].toString() else ""),
                    Pair(line.indexOf("one"), "1"),
                    Pair(line.indexOf("two"), "2"),
                    Pair(line.indexOf("three"), "3"),
                    Pair(line.indexOf("four"), "4"),
                    Pair(line.indexOf("five"), "5"),
                    Pair(line.indexOf("six"), "6"),
                    Pair(line.indexOf("seven"), "7"),
                    Pair(line.indexOf("eight"), "8"),
                    Pair(line.indexOf("nine"), "9")
            ).filter { it.key >= 0 }
                .minBy { it.key }
                .value

            val lastDigit = line.indexOfLast { it.isDigit() }
            val lastNum = mapOf(
                    Pair(lastDigit, if (lastDigit >= 0) line[lastDigit].toString() else ""),
                    Pair(line.lastIndexOf("one"), "1"),
                    Pair(line.lastIndexOf("two"), "2"),
                    Pair(line.lastIndexOf("three"), "3"),
                    Pair(line.lastIndexOf("four"), "4"),
                    Pair(line.lastIndexOf("five"), "5"),
                    Pair(line.lastIndexOf("six"), "6"),
                    Pair(line.lastIndexOf("seven"), "7"),
                    Pair(line.lastIndexOf("eight"), "8"),
                    Pair(line.lastIndexOf("nine"), "9")
            ).filter { it.key >= 0 }
                .maxBy { it.key }
                .value

            (firstNum + lastNum).toInt()
        }
    }

    val testInput = readInput("Day01_test")
    val testInput2 = readInput("Day01_test2")
    check(part1(testInput) == 142)
    check(part2(testInput2) == 281)


    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
