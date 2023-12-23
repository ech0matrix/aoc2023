import java.util.PriorityQueue
import Direction.*

data class Path(
    val position: Coordinates,
    val visited: MutableSet<Coordinates>
)

fun main() {
    val directions = listOf(NORTH, SOUTH, EAST, WEST)

    fun part1(input: List<String>): Int {
        val rowLength = input.size
        val colLength = input[0].length

        val start = Coordinates(0, 1)
        val end = Coordinates(rowLength-1, colLength-2)

        val starterPath = Path(
            Coordinates(1, 1),
            mutableSetOf(start)
        )
        val workingPaths = PriorityQueue<Path>(compareBy { 0 - it.visited.size })
        workingPaths.offer(starterPath)
        var maxPathLength = 0

        while(workingPaths.isNotEmpty()) {
            val currentPath = workingPaths.poll()!!

            if (currentPath.position == end) {
                // Found the end
                if(currentPath.visited.size > maxPathLength) {
                    // For part 2's solver, the program technically doesn't complete for some reason.
                    // But printing the answers will eventually spit out the correct answer.
                    println("Path size: ${currentPath.visited.size}")
                }
                maxPathLength = maxOf(maxPathLength, currentPath.visited.size)
//                for(row in 0..<rowLength) {
//                    for(col in 0..<colLength) {
//                        if(currentPath.visited.contains(Coordinates(row,col))) {
//                            print('O')
//                        } else {
//                            print(input[row][col])
//                        }
//                    }
//                    println()
//                }
//                println()

            } else {
                val nextPositions = when(input[currentPath.position.row][currentPath.position.col]) {
                    '.' -> {
                        directions.map { direction ->
                            currentPath.position.add(direction)
                        }
                    }
                    '>' -> listOf(currentPath.position.add(EAST))
                    'v' -> listOf(currentPath.position.add(SOUTH))
                    '<' -> listOf(currentPath.position.add(WEST))
                    '^' -> listOf(currentPath.position.add(NORTH))
                    else -> throw IllegalStateException("Unknown character")
                }.filter { position ->
                    !currentPath.visited.contains(position) && input[position.row][position.col] != '#'
                }

                if(nextPositions.size == 1) {
                    currentPath.visited.add(currentPath.position)
                    workingPaths.offer(Path(
                        nextPositions.first(),
                        currentPath.visited
                    ))
                } else {
                    nextPositions.forEach { nextPosition ->
                        val nextVisited = mutableSetOf<Coordinates>()
                        nextVisited.addAll(currentPath.visited)
                        nextVisited.add(currentPath.position)

                        val nextPath = Path(nextPosition, nextVisited)
                        workingPaths.offer(nextPath)
                    }
                }
            }
        }

        return maxPathLength
    }

    fun part2(input: List<String>): Int {
        val deslopedInput = input.map {line ->
            line.replace('>', '.')
                .replace('v', '.')
                .replace('<', '.')
                .replace('^', '.')
        }
        return part1(deslopedInput)
    }

    val testInput = readInput("Day23_test")
    timeIt {
        checkEquals(94, part1(testInput))
    }
    timeIt {
        checkEquals(154, part2(testInput))
    }

    val input = readInput("Day23")
    timeIt {
        println(part1(input))
    }
    timeIt {
        println(part2(input))
    }
}
