fun main() {
    val NORTH = Coordinates(-1,  0)
    val SOUTH = Coordinates( 1,  0)
    val EAST  = Coordinates( 0,  1)
    val WEST  = Coordinates( 0, -1)

    fun printDirection(dir: Coordinates): String {
        return when (dir) {
            NORTH -> "North"
            SOUTH -> "South"
            EAST -> "East"
            WEST -> "West"
            else -> dir.toString()
        }
    }

    val mapKey = mapOf(
            Pair('|', setOf(NORTH, SOUTH)),
            Pair('-', setOf(EAST, WEST)),
            Pair('L', setOf(NORTH, EAST)),
            Pair('J', setOf(NORTH, WEST)),
            Pair('7', setOf(SOUTH, WEST)),
            Pair('F', setOf(SOUTH, EAST))
    )

    val movedFromKey = mapOf(
            Pair(EAST, WEST),
            Pair(WEST, EAST),
            Pair(NORTH, SOUTH),
            Pair(SOUTH, NORTH)
    )

    fun part1(input: List<String>): Int {
        val rowLength = input.size
        val colLength = input[0].length

        val startRow = input.indexOfFirst { it.contains('S') }
        val startCol = input[startRow].indexOfFirst { it == 'S' }
        val start = Coordinates(startRow, startCol)
        //println("Start: $start")

        val startDirs = setOf(NORTH, SOUTH, EAST, WEST).filter { dir ->
            // Make sure directions are in range
            val moved = start.add(dir)
            moved.row in 0..<rowLength && moved.col in 0..<colLength
        }.filter { dir ->
            // Check that directions connect to start
            val moved = start.add(dir)
            val targetTile = input[moved.row][moved.col]
            val movedFrom = movedFromKey[dir]!!
            mapKey.containsKey(targetTile) && mapKey[targetTile]!!.contains(movedFrom)
        }
        checkEquals(2, startDirs.size)
        //println("StartDirs: ${startDirs.map{ printDirection(it) }}")

        // Traverse loop once
        var numSteps = 1
        var moveDir = startDirs.first()
        var current = start.add(moveDir)
        while (current != start) {
            //println("Current: $current, Start: $start")
            val movedFrom = movedFromKey[moveDir]!!
            val nextTile = input[current.row][current.col]
            moveDir = mapKey[nextTile]!!.first { it != movedFrom }
            current = current.add(moveDir)
            numSteps += 1
        }

        return numSteps / 2
    }

    fun part2(input: List<String>): Int {
        val rowLength = input.size
        val colLength = input[0].length

        val startRow = input.indexOfFirst { it.contains('S') }
        val startCol = input[startRow].indexOfFirst { it == 'S' }
        val start = Coordinates(startRow, startCol)
        //println("Start: $start")

        val startDirs = setOf(NORTH, SOUTH, EAST, WEST).filter { dir ->
            // Make sure directions are in range
            val moved = start.add(dir)
            moved.row in 0..<rowLength && moved.col in 0..<colLength
        }.filter { dir ->
            // Check that directions connect to start
            val moved = start.add(dir)
            val targetTile = input[moved.row][moved.col]
            val movedFrom = movedFromKey[dir]!!
            mapKey.containsKey(targetTile) && mapKey[targetTile]!!.contains(movedFrom)
        }
        checkEquals(2, startDirs.size)
        //println("StartDirs: ${startDirs.map{ printDirection(it) }}")

        // Traverse loop once
        val loopTiles = mutableSetOf(start) // Record all tiles
        var numSteps = 1
        var moveDir = startDirs.first()
        var current = start.add(moveDir)
        while (current != start) {
            loopTiles.add(current)
            //println("Current: $current, Start: $start")
            val movedFrom = movedFromKey[moveDir]!!
            val nextTile = input[current.row][current.col]
            moveDir = mapKey[nextTile]!!.first { it != movedFrom }
            current = current.add(moveDir)
            numSteps += 1
        }

        // Scan line fill
        var isInside = false
        var count = 0
        for(row in 0..<rowLength) {
            isInside = false
            for(col in 0..<colLength) {
                val current = Coordinates(row, col)
                if (loopTiles.contains(current)) {
                    if (input[row][col] != '-' && input[row][col] != 'L' && input[row][col] != 'J') {
                        isInside = !isInside
                    }
                    print(input[row][col])
                } else if (isInside) {
                    count += 1
                    print("X")
                } else {
                    print(input[row][col])
                }
            }
            println()
        }

        return count
    }

    checkEquals(4, part1(readInput("Day10_test")))
    checkEquals(8, part1(readInput("Day10_test2")))

    checkEquals(4, part2(readInput("Day10_test3")))
    checkEquals(4, part2(readInput("Day10_test4")))
    checkEquals(8, part2(readInput("Day10_test5")))
    checkEquals(10, part2(readInput("Day10_test6")))

    val input = readInput("Day10")
    println(part1(input))
    println(part2(input))
}
