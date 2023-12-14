import kotlin.system.measureTimeMillis

fun main() {
    fun part1(input: List<String>): Int {
        val rowLength = input.size
        val colLength = input[0].length

        val roundRocks = mutableSetOf<Coordinates>()
        val squareRocks = mutableSetOf<Coordinates>()

        for(row in 0..<rowLength) {
            for(col in 0..<colLength) {
                val c = input[row][col]
                if (c == '#') {
                    squareRocks.add(Coordinates(row, col))
                } else if (c == 'O') {
                    roundRocks.add(Coordinates(row, col))
                }
            }
        }

        var moveRocks = true
        while(moveRocks) {
            moveRocks = false
            val checkRocks = roundRocks.toList()
            for (rock in checkRocks) {
                if (rock.row > 0) {
                    val checkSpot = Coordinates(rock.row-1, rock.col)
                    if (!squareRocks.contains(checkSpot) && !roundRocks.contains(checkSpot)) {
                        moveRocks = true
                        roundRocks.remove(rock)
                        roundRocks.add(checkSpot)
                    }
                }
            }
        }

//        for(row in 0..<rowLength) {
//            for(col in 0..<colLength) {
//                val spot = Coordinates(row, col)
//                if (squareRocks.contains(spot)) {
//                    print('#')
//                } else if (roundRocks.contains(spot)) {
//                    print('O')
//                } else {
//                    print('.')
//                }
//            }
//            println()
//        }

        return roundRocks.sumOf {
            rowLength - it.row
        }
    }

    fun part2(input: List<String>): Int {
        val rowLength = input.size
        val colLength = input[0].length

        val roundRocks = mutableSetOf<Coordinates>()
        val squareRocks = mutableSetOf<Coordinates>()

        for(row in 0..<rowLength) {
            for(col in 0..<colLength) {
                val c = input[row][col]
                if (c == '#') {
                    squareRocks.add(Coordinates(row, col))
                } else if (c == 'O') {
                    roundRocks.add(Coordinates(row, col))
                }
            }
        }

        val cache = mutableMapOf<Set<Coordinates>, Int>()
        cache[roundRocks.toSet()] = 0

        for(cycle in 1..1000000000) {
            // Roll North
            var moveRocks = true
            while (moveRocks) {
                moveRocks = false
                val checkRocks = roundRocks.toList()
                for (rock in checkRocks) {
                    if (rock.row > 0) {
                        val checkSpot = Coordinates(rock.row - 1, rock.col)
                        if (!squareRocks.contains(checkSpot) && !roundRocks.contains(checkSpot)) {
                            moveRocks = true
                            roundRocks.remove(rock)
                            roundRocks.add(checkSpot)
                        }
                    }
                }
            }

            // Roll West
            moveRocks = true
            while (moveRocks) {
                moveRocks = false
                val checkRocks = roundRocks.toList()
                for (rock in checkRocks) {
                    if (rock.col > 0) {
                        val checkSpot = Coordinates(rock.row, rock.col - 1)
                        if (!squareRocks.contains(checkSpot) && !roundRocks.contains(checkSpot)) {
                            moveRocks = true
                            roundRocks.remove(rock)
                            roundRocks.add(checkSpot)
                        }
                    }
                }
            }

            // Roll South
            moveRocks = true
            while (moveRocks) {
                moveRocks = false
                val checkRocks = roundRocks.toList()
                for (rock in checkRocks) {
                    if (rock.row < rowLength - 1) {
                        val checkSpot = Coordinates(rock.row + 1, rock.col)
                        if (!squareRocks.contains(checkSpot) && !roundRocks.contains(checkSpot)) {
                            moveRocks = true
                            roundRocks.remove(rock)
                            roundRocks.add(checkSpot)
                        }
                    }
                }
            }

            // Roll East
            moveRocks = true
            while (moveRocks) {
                moveRocks = false
                val checkRocks = roundRocks.toList()
                for (rock in checkRocks) {
                    if (rock.col < colLength - 1) {
                        val checkSpot = Coordinates(rock.row, rock.col + 1)
                        if (!squareRocks.contains(checkSpot) && !roundRocks.contains(checkSpot)) {
                            moveRocks = true
                            roundRocks.remove(rock)
                            roundRocks.add(checkSpot)
                        }
                    }
                }
            }


            if (cycle % 10000 == 0) {
                println("Cycle $cycle")
            }

            val signature = roundRocks.toSet()
            if (cache.containsKey(signature)) {
                val detectedCycle = cache[signature]!!
                println("**Detected loop. $cycle same as $detectedCycle")
                val loopSize = cycle - detectedCycle
                val initialCycles = detectedCycle
                val cycleState = ((1000000000 - initialCycles) % loopSize) + initialCycles
                val endRocks = cache.filterValues { it == cycleState }.keys.first()
                return endRocks.sumOf {
                    rowLength - it.row
                }
            } else {
                cache[roundRocks.toSet()] = cycle
            }

//            for (row in 0..<rowLength) {
//                for (col in 0..<colLength) {
//                    val spot = Coordinates(row, col)
//                    if (squareRocks.contains(spot)) {
//                        print('#')
//                    } else if (roundRocks.contains(spot)) {
//                        print('O')
//                    } else {
//                        print('.')
//                    }
//                }
//                println()
//            }
        }

        return roundRocks.sumOf {
            rowLength - it.row
        }
    }

    val testInput = readInput("Day14_test")
    checkEquals(136, part1(testInput))
    checkEquals(64, part2(testInput))

    val input = readInput("Day14")
    println(part1(input))
    println(part2(input))
}
