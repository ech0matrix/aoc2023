fun main() {
    val NORTH = Coordinates(-1,  0)
    val SOUTH = Coordinates( 1,  0)
    val EAST  = Coordinates( 0,  1)
    val WEST  = Coordinates( 0, -1)

    data class LightBeam(
            val position: Coordinates,
            val direction: Coordinates
    )

    fun energize(input: List<String>, startingBeam: LightBeam): Int {
        val rowLength = input.size
        val colLength = input[0].length
        val beams = mutableSetOf<LightBeam>()
        beams.add(startingBeam)
        val energized = mutableSetOf<LightBeam>()

        while(beams.isNotEmpty()) {
            val beam = beams.first()
            beams.remove(beam)
            if (!energized.contains(beam) && (beam.position.row in 0..<rowLength && beam.position.col in 0..<colLength)) {
                energized.add(beam)
                when (input[beam.position.row][beam.position.col]) {
                    '.' -> {
                        beams.add(beam.copy(position = beam.position.add(beam.direction)))
                    }

                    '/' -> {
                        when (beam.direction) {
                            NORTH -> beams.add(beam.copy(direction = EAST, position = beam.position.add(EAST)))
                            SOUTH -> beams.add(beam.copy(direction = WEST, position = beam.position.add(WEST)))
                            EAST -> beams.add(beam.copy(direction = NORTH, position = beam.position.add(NORTH)))
                            WEST -> beams.add(beam.copy(direction = SOUTH, position = beam.position.add(SOUTH)))
                            else -> throw IllegalStateException()
                        }
                    }

                    '\\' -> {
                        when (beam.direction) {
                            NORTH -> beams.add(beam.copy(direction = WEST, position = beam.position.add(WEST)))
                            SOUTH -> beams.add(beam.copy(direction = EAST, position = beam.position.add(EAST)))
                            EAST -> beams.add(beam.copy(direction = SOUTH, position = beam.position.add(SOUTH)))
                            WEST -> beams.add(beam.copy(direction = NORTH, position = beam.position.add(NORTH)))
                            else -> throw IllegalStateException()
                        }
                    }

                    '|' -> {
                        when (beam.direction) {
                            NORTH, SOUTH -> beams.add(beam.copy(position = beam.position.add(beam.direction)))
                            EAST, WEST -> {
                                beams.add(beam.copy(direction = NORTH, position = beam.position.add(NORTH)))
                                beams.add(beam.copy(direction = SOUTH, position = beam.position.add(SOUTH)))
                            }

                            else -> throw IllegalStateException()
                        }
                    }

                    '-' -> {
                        when (beam.direction) {
                            WEST, EAST -> beams.add(beam.copy(position = beam.position.add(beam.direction)))
                            NORTH, SOUTH -> {
                                beams.add(beam.copy(direction = WEST, position = beam.position.add(WEST)))
                                beams.add(beam.copy(direction = EAST, position = beam.position.add(EAST)))
                            }

                            else -> throw IllegalStateException()
                        }
                    }

                    else -> throw IllegalStateException("Unexpected character: ${input[beam.position.row][beam.position.col]}")
                }
            }
        }

//        for(row in 0..<rowLength) {
//            for(col in 0..<colLength) {
//                if (energized.any { it.position.row == row && it.position.col == col }) {
//                    print("#")
//                } else {
//                    print(".")
//                }
//            }
//            println()
//        }

        return energized.map { it.position }.toSet().size
    }

    fun part1(input: List<String>): Int {
        return energize(input, LightBeam(Coordinates(0, 0), Coordinates(0, 1)))
    }

    fun part2(input: List<String>): Int {
        val rowLength = input.size
        val colLength = input[0].length
        val edgeBeams = mutableListOf<LightBeam>()
        for(col in 0..<colLength) {
            edgeBeams.add(LightBeam(Coordinates(0, col), SOUTH))

            edgeBeams.add(LightBeam(Coordinates(rowLength-1, col), NORTH))
        }
        for(row in 0..<rowLength) {
            edgeBeams.add(LightBeam(Coordinates(row, 0), EAST))

            edgeBeams.add(LightBeam(Coordinates(row, colLength-1), WEST))
        }
        return edgeBeams.maxOf { energize(input, it) }
    }

    val testInput = readInput("Day16_test")
    checkEquals(46, part1(testInput))
    checkEquals(51, part2(testInput))

    val input = readInput("Day16")
    println(part1(input))
    println(part2(input))
}
