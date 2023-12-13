import java.lang.IllegalStateException

fun main() {
    fun parseTerrain(input: List<String>): List<List<String>> {
        val maps = mutableListOf<List<String>>()
        val currentMap = mutableListOf<String>()
        for(line in input) {
            if (line.isEmpty()) {
                maps.add(currentMap.toList())
                currentMap.clear()
            } else {
                currentMap.add(line)
            }
        }
        if (currentMap.isNotEmpty()) {
            maps.add(currentMap.toList())
        }
        return maps.toList()
    }

    data class RockMap(
        val rows: List<Set<Int>>,
        val cols: List<Set<Int>>,
        val originalMap: List<String>,
        val originalIndex: Int
    )

    fun createRockMap(rawMap: List<String>, index: Int): RockMap {
        val rows = mutableMapOf<Int, MutableSet<Int>>()
        val cols = mutableMapOf<Int, MutableSet<Int>>()
        val rowLength = rawMap.size
        val colLength = rawMap[0].length
        for(row in 0..<rowLength) {
            //println(rawMap[row])
            rows[row] = mutableSetOf()
            for(col in 0..<colLength) {
                if (row == 0) {
                    cols[col] = mutableSetOf()
                }

                if (rawMap[row][col] == '#') {
                    rows[row]!!.add(col)
                    cols[col]!!.add(row)
                }
            }
        }
        //println()

        val mapByRows = rows.entries.sortedBy { it.key }.map { it.value.toSet() }
        val mapByCols = cols.entries.sortedBy { it.key }.map { it.value.toSet() }

//        println("Rows: ")
//        mapByRows.forEachIndexed { i, r ->
//            println("$i) $r")
//        }
//        println("Cols: ")
//        mapByCols.forEachIndexed { i, r ->
//            println("$i) $r")
//        }
//        println()

        return RockMap(mapByRows, mapByCols, rawMap, index)
    }

    fun findReflection(rows: List<Set<Int>>): Int {
        var splitAfter = 0
        while(splitAfter < rows.size-1) {
            val beforeSplit = rows.subList(0, splitAfter+1).asReversed()
            val afterSplit  = rows.subList(splitAfter+1, rows.size)
            if(beforeSplit.zip(afterSplit).all { it.first == it.second }) {
                return splitAfter
            }
            splitAfter += 1
        }
        return -1
    }

    fun part1(input: List<String>): Int {
        val rawMaps = parseTerrain(input)
        val maps = rawMaps.mapIndexed{ i, rawMap -> createRockMap(rawMap, i) }
        return maps.sumOf { map ->
            val rowReflection = findReflection(map.rows)
            if (rowReflection >= 0) {
                (rowReflection+1)*100
            } else {
                val colReflection = findReflection(map.cols)
                if (colReflection >= 0) {
                    colReflection+1
                } else {
                    println("Did not find reflection in map ${map.originalIndex}")
                    map.originalMap.forEach { println(it) }
                    println()
                    println("Rows: ")
                    map.rows.forEachIndexed { i, r ->
                        println("$i) $r")
                    }
                    println("Cols: ")
                    map.cols.forEachIndexed { i, r ->
                        println("$i) $r")
                    }
                    println()
                    throw IllegalStateException("Did not find reflection in map")
                }
            }
        }
    }

    fun findSmudgedReflection(rows: List<Set<Int>>): Int {
        var splitAfter = 0
        while(splitAfter < rows.size-1) {
            val beforeSplit = rows.subList(0, splitAfter+1).asReversed()
            val afterSplit  = rows.subList(splitAfter+1, rows.size)
            val reflectionPairs = beforeSplit.zip(afterSplit)
            val smudgeCount = reflectionPairs.map { (first, second) ->
                if (first.size == second.size) {
                    if (first == second) {
                        0
                    } else {
                        2
                    }
                } else {
                    val pair = listOf(first, second)
                    val bigger = pair.maxBy { it.size }
                    val smaller = pair.minBy { it.size }
                    if ((bigger.size - smaller.size) != 1) {
                        2
                    } else {
                        if (bigger.containsAll(smaller)) {
                            1
                        } else {
                            2
                        }
                    }
                }
            }.sum()

            if(smudgeCount == 1) {
                return splitAfter
            }
            splitAfter += 1
        }
        return -1
    }

    fun part2(input: List<String>): Int {
        val rawMaps = parseTerrain(input)
        val maps = rawMaps.mapIndexed{ i, rawMap -> createRockMap(rawMap, i) }
        return maps.sumOf { map ->
            val rowReflection = findSmudgedReflection(map.rows)
            if (rowReflection >= 0) {
                (rowReflection+1)*100
            } else {
                val colReflection = findSmudgedReflection(map.cols)
                if (colReflection >= 0) {
                    colReflection+1
                } else {
                    println("Did not find reflection in map ${map.originalIndex}")
                    map.originalMap.forEach { println(it) }
                    println()
                    println("Rows: ")
                    map.rows.forEachIndexed { i, r ->
                        println("$i) $r")
                    }
                    println("Cols: ")
                    map.cols.forEachIndexed { i, r ->
                        println("$i) $r")
                    }
                    println()
                    throw IllegalStateException("Did not find reflection in map")
                }
            }
        }
    }

//    val debugInput = readInput("Day13_debug")
//    println("Debug: ${part1(debugInput)}")

    val testInput = readInput("Day13_test")
    checkEquals(405, part1(testInput))
    checkEquals(400, part2(testInput))

    val input = readInput("Day13")
    println(part1(input))
    println(part2(input))
}
