fun main() {
    fun printGalaxies(galaxies: Set<LongCoordinates>) {
        val maxRow = galaxies.maxOf { it.row }
        val maxCol = galaxies.maxOf { it.col }
        for(row in 0..maxRow) {
            for(col in 0..maxCol) {
                if(galaxies.contains(LongCoordinates(row, col))) {
                    print('#')
                } else {
                    print('.')
                }
            }
            println()
        }
        println()
        println()
    }

    fun solve(input: List<String>, expandBy: Long): Long {
        val rowLength = input.size.toLong()
        val colLength = input[0].length.toLong()

        // Parse input
        val galaxies = mutableSetOf<LongCoordinates>()
        input.forEachIndexed { row, line ->
            line.forEachIndexed { col, c ->
                if (c == '#') {
                    galaxies.add(LongCoordinates(row.toLong(), col.toLong()))
                }
            }
        }

        // Expand empty rows
        for(row in rowLength-1 downTo 0L) {
            val shouldExpand = galaxies.none { it.row == row }
            if (shouldExpand) {
                val expandSet = galaxies.filter { it.row > row }.toSet()
                galaxies.removeAll(expandSet)
                val expanded = expandSet.map { it.add(LongCoordinates(expandBy, 0)) }.toSet()
                galaxies.addAll(expanded)
            }
        }

        // Expand empty cols
        for(col in colLength-1 downTo 0L) {
            val shouldExpand = galaxies.none { it.col == col }
            if (shouldExpand) {
                val expandSet = galaxies.filter { it.col > col }.toSet()
                galaxies.removeAll(expandSet)
                val expanded = expandSet.map { it.add(LongCoordinates(0, expandBy)) }.toSet()
                galaxies.addAll(expanded)
            }
        }

        //printGalaxies(galaxies)

        // Generate all pairs
        val pairs = galaxies.flatMap { galaxy ->
            galaxies.filter{ it != galaxy }.map { galaxy2 ->
                setOf(galaxy, galaxy2)
            }.toSet()
        }.toSet()

        return pairs.sumOf { pair ->
            pair.first().manhattanDistance(pair.last())
        }
    }

    val testInput = readInput("Day11_test")
    checkEquals(374, solve(testInput, 1L))
    checkEquals(1030, solve(testInput, 9L))
    checkEquals(8410, solve(testInput, 99L))

    val input = readInput("Day11")
    println(solve(input, 1L))
    println(solve(input, 999999L))
}
