import Direction.*

fun main() {
    val directions = listOf(NORTH, SOUTH, EAST, WEST)

    fun part1(input: List<String>, numSteps: Int): Int {
        val rowLength = input.size
        val colLength = input[0].length
        var start: Coordinates? = null
        val rocks = mutableSetOf<Coordinates>()

        for(row in 0..<rowLength) {
            for(col in 0..<colLength) {
                if (input[row][col] == '#') {
                    rocks.add(Coordinates(row, col))
                } else if (input[row][col] == 'S') {
                    start = Coordinates(row, col)
                }
            }
        }
        check(start != null)

        var stepCount = 0
        var currentPositions = mutableSetOf(start)
        while(stepCount < numSteps) {
            stepCount += 1
            val nextPositions = mutableSetOf<Coordinates>()
            currentPositions.forEach { current ->
                directions.forEach { direction ->
                    val next = current.add(direction)
                    if (!rocks.contains(next)) {
                        nextPositions.add(next)
                    }
                }
            }
            currentPositions = nextPositions
        }

        return currentPositions.size
    }

    fun part2(input: List<String>, numSteps: Int): Int {
        val rowLength = input.size
        val colLength = input[0].length
        var start: LongCoordinates? = null
        val rocks = mutableSetOf<LongCoordinates>()

        for(row in 0..<rowLength) {
            for(col in 0..<colLength) {
                if (input[row][col] == '#') {
                    rocks.add(LongCoordinates(row.toLong(), col.toLong()))
                } else if (input[row][col] == 'S') {
                    start = LongCoordinates(row.toLong(), col.toLong())
                }
            }
        }
        check(start != null)

        var stepCount = 0
        var currentPositions = mutableSetOf(start!!)
        while(stepCount < numSteps) {
            stepCount += 1
            val nextPositions = mutableSetOf<LongCoordinates>()
            currentPositions.forEach { current ->
                directions.forEach { direction ->
                    val next = current.add(direction)
                    var rockRow = next.row % rowLength
                    var rockCol = next.col % colLength
                    if (rockRow < 0) rockRow += rowLength
                    if (rockCol < 0) rockCol += colLength
                    if (!rocks.contains(LongCoordinates(rockRow, rockCol))) {
                        nextPositions.add(next)
                    }
                }
            }
            currentPositions = nextPositions
        }

        return currentPositions.size
    }

    val testInput = readInput("Day21_test")
    checkEquals(16, part1(testInput, 6))

//    checkEquals(16, part2(testInput, 6))
//    checkEquals(50, part2(testInput, 10))
//    checkEquals(1594, part2(testInput, 50))
//    checkEquals(6536, part2(testInput, 100))
//    checkEquals(167004, part2(testInput, 500))
//    checkEquals(668697, part2(testInput, 1000))
//    checkEquals(16733044, part2(testInput, 5000))

    val input = readInput("Day21")
    println(part1(input, 64))
    timeIt {
        println("65 steps: ${part2(input, 65)}")
    }
    timeIt {
        println("196 steps: ${part2(input, 196)}")
    }
    timeIt {
        println("327 steps: ${part2(input, 327)}")
    }
    timeIt {
        println("458 steps: ${part2(input, 458)}")
    }
}
