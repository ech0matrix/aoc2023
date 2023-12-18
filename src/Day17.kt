import java.lang.IllegalStateException
import java.util.PriorityQueue

fun main() {
    val NORTH = Coordinates(-1,  0)
    val SOUTH = Coordinates( 1,  0)
    val EAST  = Coordinates( 0,  1)
    val WEST  = Coordinates( 0, -1)

    data class Tile(
            val position: Coordinates,
            val direction: Coordinates,
            val heatLoss: Int,
            val directionStreak: Int
    )

    data class VisitedTile(
            val position: Coordinates,
            val direction: Coordinates,
            val directionStreak: Int
    )

    fun part1(input: List<String>): Int {
        val rowLength = input.size
        val colLength = input[0].length

        fun isInBounds(position: Coordinates): Boolean {
            return ((position.row in 0..<rowLength) && (position.col in 0..<colLength))
        }

        val heatMap = mutableMapOf<Coordinates, Int>()
        for(row in 0..<rowLength) {
            for(col in 0..<colLength) {
                heatMap[Coordinates(row, col)] = input[row][col].toString().toInt()
            }
        }
        val start = Coordinates(0, 0)
        val end = Coordinates(rowLength-1, colLength-1)

        var leastHeatLoss = Int.MAX_VALUE
        val visited = mutableSetOf<VisitedTile>()
        val active = PriorityQueue<Tile>(compareBy { it.heatLoss })
        active.offer(Tile(start, EAST, 0, 0))

        while(!active.isEmpty()) {
            // Get next tile
            val current = active.poll()!!

            if (current.position == end) {
                // Found the end
                leastHeatLoss = minOf(current.heatLoss, leastHeatLoss)
            }

            if (current.heatLoss < leastHeatLoss) {
                val rightTurns = when(current.direction) {
                    NORTH, SOUTH -> listOf(WEST, EAST)
                    EAST, WEST -> listOf(NORTH, SOUTH)
                    else -> throw IllegalStateException("Unexpected direction")
                }

                for(turn in rightTurns) {
                    val nextPosition = current.position.add(turn)
                    if(isInBounds(nextPosition)) {
                        val next = Tile(
                                nextPosition,
                                turn,
                                current.heatLoss + heatMap[nextPosition]!!,
                                1
                        )
                        val key = VisitedTile(next.position, next.direction, next.directionStreak)
                        val doesContain = visited.contains(key)
                        if(!doesContain) {
                            visited.add(key)
                            active.offer(next)
                        }
                    }
                }

                if (current.directionStreak < 3) {
                    val nextPosition = current.position.add(current.direction)
                    if(isInBounds(nextPosition)) {
                        val next = Tile(
                                nextPosition,
                                current.direction,
                                current.heatLoss + heatMap[nextPosition]!!,
                                current.directionStreak + 1
                        )
                        val key = VisitedTile(next.position, next.direction, next.directionStreak)
                        val doesContain = visited.contains(key)
                        if(!doesContain) {
                            visited.add(key)
                            active.offer(next)
                        }
                    }
                }
            }
        }

        return leastHeatLoss
    }

    fun part2(input: List<String>): Int {
        val rowLength = input.size
        val colLength = input[0].length

        fun isInBounds(position: Coordinates): Boolean {
            return ((position.row in 0..<rowLength) && (position.col in 0..<colLength))
        }

        val heatMap = mutableMapOf<Coordinates, Int>()
        for(row in 0..<rowLength) {
            for(col in 0..<colLength) {
                heatMap[Coordinates(row, col)] = input[row][col].toString().toInt()
            }
        }
        val start = Coordinates(0, 0)
        val end = Coordinates(rowLength-1, colLength-1)

        var leastHeatLoss = Int.MAX_VALUE
        val visited = mutableSetOf<VisitedTile>()
        val active = PriorityQueue<Tile>(compareBy { it.heatLoss })
        active.offer(Tile(start, EAST, 0, 0))

        while(!active.isEmpty()) {
            // Get next tile
            val current = active.poll()!!

            if (current.position == end && current.directionStreak >= 4) {
                // Found the end
                leastHeatLoss = minOf(current.heatLoss, leastHeatLoss)
            }

            if (current.heatLoss < leastHeatLoss) {
                val rightTurns = when(current.direction) {
                    NORTH, SOUTH -> listOf(WEST, EAST)
                    EAST, WEST -> listOf(NORTH, SOUTH)
                    else -> throw IllegalStateException("Unexpected direction")
                }

                if (current.directionStreak >= 4) {
                    for (turn in rightTurns) {
                        val nextPosition = current.position.add(turn)
                        if (isInBounds(nextPosition)) {
                            val next = Tile(
                                    nextPosition,
                                    turn,
                                    current.heatLoss + heatMap[nextPosition]!!,
                                    1
                            )
                            val key = VisitedTile(next.position, next.direction, next.directionStreak)
                            val doesContain = visited.contains(key)
                            if (!doesContain) {
                                visited.add(key)
                                active.offer(next)
                            }
                        }
                    }
                }

                if (current.directionStreak < 10) {
                    val nextPosition = current.position.add(current.direction)
                    if(isInBounds(nextPosition)) {
                        val next = Tile(
                                nextPosition,
                                current.direction,
                                current.heatLoss + heatMap[nextPosition]!!,
                                current.directionStreak + 1
                        )
                        val key = VisitedTile(next.position, next.direction, next.directionStreak)
                        val doesContain = visited.contains(key)
                        if(!doesContain) {
                            visited.add(key)
                            active.offer(next)
                        }
                    }
                }
            }
        }

        return leastHeatLoss
    }

    val testInput = readInput("Day17_test")
    timeIt {
        checkEquals(102, part1(testInput))
    }
    timeIt {
        checkEquals(94, part2(testInput))
    }

    val input = readInput("Day17")
    timeIt {
        println(part1(input))
    }
    timeIt {
        println(part2(input))
    }
}
