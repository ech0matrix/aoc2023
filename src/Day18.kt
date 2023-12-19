import Direction.*
import kotlin.math.abs

@OptIn(ExperimentalStdlibApi::class)
fun main() {
    data class Command(
        val direction: Direction,
        val distance: Int,
        val edgeColor: String
    ) {
        override fun toString(): String {
            // R 6 (#70c710)
            val d = when(direction) {
                NORTH -> "U"
                SOUTH -> "D"
                EAST -> "R"
                WEST -> "L"
            }

            return "$d $distance (#$edgeColor)"
        }
    }

    fun parseDirection(input: String): Direction {
        return when(input) {
            "U" -> NORTH
            "D" -> SOUTH
            "R" -> EAST
            "L" -> WEST
            else -> throw IllegalStateException("Unexpected direction: $input")
        }
    }

    fun parseCommands(input: List<String>): List<Command> {
        return input.map { line ->
            val parts = line.split(" ", "#", "(", ")").filter { it.isNotBlank() }
            checkEquals(3, parts.size)
            val direction = parseDirection(parts[0])
            Command(direction, parts[1].toInt(), parts[2])
        }
    }

    fun paintWall(blockCoordinates: Coordinates, wallFace: Direction, color: String, blocks: MutableMap<Coordinates, MutableMap<Direction, String>>) {
        if (!blocks.containsKey(blockCoordinates)) {
            blocks[blockCoordinates] = mutableMapOf()
        }
        blocks[blockCoordinates]!![wallFace] = color
    }

    fun paintEdges(command: Command, current: Coordinates, holes: MutableSet<Coordinates>, blocks: MutableMap<Coordinates, MutableMap<Direction, String>>) {
        when (command.direction) {
            NORTH, SOUTH -> {
                val westEdge = current.add(WEST)
                if (!holes.contains(westEdge)) {
                    paintWall(westEdge, EAST, command.edgeColor, blocks)
                }
                val eastEdge = current.add(EAST)
                if (!holes.contains(eastEdge)) {
                    paintWall(eastEdge, WEST, command.edgeColor, blocks)
                }
            }
            EAST, WEST -> {
                val northEdge = current.add(NORTH)
                if (!holes.contains(northEdge)) {
                    paintWall(northEdge, SOUTH, command.edgeColor, blocks)
                }
                val southEdge = current.add(SOUTH)
                if (!holes.contains(southEdge)) {
                    paintWall(southEdge, NORTH, command.edgeColor, blocks)
                }
            }
        }
    }

    fun getLimits(holes: MutableSet<Coordinates>): Pair<Coordinates, Coordinates> {
        val minRow = holes.minOf { it.row }
        val minCol = holes.minOf { it.col }
        val maxRow = holes.maxOf { it.row }
        val maxCol = holes.maxOf { it.col }
        return Pair(Coordinates(minRow, minCol), Coordinates(maxRow, maxCol))
    }

    fun getLimits(holes: MutableSet<LongCoordinates>): Pair<LongCoordinates, LongCoordinates> {
        val minRow = holes.minOf { it.row }
        val minCol = holes.minOf { it.col }
        val maxRow = holes.maxOf { it.row }
        val maxCol = holes.maxOf { it.col }
        return Pair(LongCoordinates(minRow, minCol), LongCoordinates(maxRow, maxCol))
    }

    fun printHoles(holes: MutableSet<Coordinates>) {
        val (min, max) = getLimits(holes)
        for(row in min.row..max.row) {
            for(col in min.col..max.col) {
                if (holes.contains(Coordinates(row, col))) {
                    print("#")
                } else {
                    print(".")
                }
            }
            println()
        }
    }

    fun fill(fillStart: Set<Coordinates>, limits: Pair<Coordinates, Coordinates>, holes: MutableSet<Coordinates>, blocks: MutableMap<Coordinates, MutableMap<Direction, String>>) {
        val (min, max) = limits
        var toFill = fillStart
        while(toFill.isNotEmpty()) {
            var nextFill = mutableSetOf<Coordinates>()

            for(fill in toFill) {
                holes.add(fill)
                blocks.remove(fill)
                listOf(NORTH, SOUTH, WEST, EAST).forEach { dir ->
                    val test = fill.add(dir)
                    if (!holes.contains(test)
                            && test.row >= min.row
                            && test.row <= max.row
                            && test.col >= min.col
                            && test.col <= max.col
                    ) {
                        nextFill.add(test)
                    }
                }
            }

            toFill = nextFill
        }
    }

    fun part1(input: List<String>): Int {
        val commands = parseCommands(input)

        val start = Coordinates(0,0)
        var current = start
        val blocks = mutableMapOf<Coordinates, MutableMap<Direction, String>>()
        val holes = mutableSetOf(current)

        // Cut edges
        for(command in commands) {
            // Paint edges
            paintEdges(command, current, holes, blocks)

            // Dig hole
            repeat(command.distance) {
                current = current.add(command.direction)
                holes.add(current)
                blocks.remove(current)
                paintEdges(command, current, holes, blocks)
            }
        }

//        printHoles(holes)
//        println()

        // Fill inside
        fill(setOf(Coordinates(1, 1)), getLimits(holes), holes, blocks)
        //printHoles(holes)

        return holes.size
    }

    fun part1Improved(input: List<String>): Long {
        val commands = parseCommands(input)

        val start = Coordinates(0,0)
        var current = start
        val vertices = mutableListOf(start)
        var perimeter = 0L
        for(command in commands) {
            current = current.add(command.direction.withDistance(command.distance))
            vertices.add(current)
            perimeter += command.distance
        }
        check(current == start)

        var pairs1 = 0L
        var pairs2 = 0L
        for(i in 0..<vertices.size-1) {
            val vertex1 = vertices[i]
            val vertex2 = vertices[i+1]
            pairs1 += vertex1.col.toLong() * vertex2.row.toLong()
            pairs2 += vertex1.row.toLong() * vertex2.col.toLong()
        }

        var area = abs(pairs1 - pairs2) / 2L

        // We have to add back half the perimeter, since the vertices
        // are actually in the middle of trenches, and not the corners.
        area += perimeter/2 + 1L

        return area
    }

    fun part2(input: List<String>): Long {
        val encodedCommands = parseCommands(input).map { it.edgeColor }

        val newCommands = encodedCommands.map { encodedCommand ->
            val distance = encodedCommand.dropLast(1).hexToInt()
            val direction = when(encodedCommand.takeLast(1)) {
                "0" -> EAST
                "1" -> SOUTH
                "2" -> WEST
                "3" -> NORTH
                else -> throw IllegalStateException("Unexpected direction: ${encodedCommand.take(1)}")
            }
            Command(direction, distance, encodedCommand)
        }

        return part1Improved(newCommands.map {it.toString()})
    }

    val testInput = readInput("Day18_test")
    checkEquals(62, part1(testInput))
    checkEquals(62L, part1Improved(testInput))
    checkEquals(952408144115L, part2(testInput))

    val input = readInput("Day18")
    timeIt {
        println(part1(input))
    }
    timeIt {
        println(part1Improved(input))
    }

    timeIt {
        println(part2(input))
    }
}
