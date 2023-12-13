import java.lang.IllegalStateException

fun main() {
    fun generateCombinations(numBits: Int, combinationCache: MutableMap<Int, Set<List<Boolean>>>): Set<List<Boolean>> {
        if (combinationCache.containsKey(numBits)) {
            return combinationCache[numBits]!!
        }

        if (numBits == 1) {
            val singleBit = setOf(listOf(true), listOf(false))
            combinationCache[numBits] = singleBit
            return singleBit
        }

        val smallerBits = generateCombinations(numBits-1, combinationCache)
        val withFalse = smallerBits.map { listOf(false) + it }.toSet()
        val withTrue =  smallerBits.map { listOf(true) + it }.toSet()
        val newCombinations = withFalse + withTrue
        combinationCache[numBits] = newCombinations
        return newCombinations
    }

    fun isValid(rowTest: String, groups: List<Int>): Boolean {
        val testGroups = rowTest.split('.').filterNot { it.isBlank() }
        if (testGroups.size != groups.size) {
            return false
        }
        // All split groups match sizes of damage groups
        return testGroups.map{ it.length }.zip(groups).all { it.first == it.second }
    }

    fun part1(input: List<String>): Long {
        val combinationCache = mutableMapOf<Int, Set<List<Boolean>>>()

        return input.sumOf { line ->
            // Parse line
            val parts = line.split(' ')
            val row = parts[0]
            val groups = parts[1].split(',').map { it.toInt() }
            val checksum = groups.sum()

            //println("$row -> $groups ($checksum)")

            // Get counts
            val unknownCount = row.count { it == '?' }
            val missingDamage = checksum - row.count { it == '#' }
            //println("  Unknown: $unknownCount, Missing: $missingDamage")

            if (unknownCount == 0 || missingDamage == 0) {
                return@sumOf 1L
            }

            val combinations = generateCombinations(unknownCount, combinationCache).filter { combination -> combination.count { it } == missingDamage }
            //println("    Combinations to check: ${combinations.size}")
            //combinations.forEach { println("    $it") }
            val unknownIndexes = row.indices.filter { row[it] == '?' }
            //println("    $unknownIndexes")
            combinations.count { combination: List<Boolean> ->
                val pairs = unknownIndexes.zip(combination)
                var rowTest = row
                pairs.forEach { (index, value) ->
                    val c = if(value) '#' else '.'
                    rowTest = rowTest.replaceRange(index, index+1, c.toString())
                }
                //println("    $rowTest")
                isValid(rowTest, groups).also {
                    //if (it) println("   $rowTest")
                }
            }.toLong()
        }
    }

    data class Row(
            val row: String,
            val groups: List<Int>
    ) {
        val checksum = groups.sum()
        val unknownCount = row.count { it == '?' }
        val missingDamage = checksum - row.count { it == '#' }
    }

    data class CombinationSignature(
            val rowRemainder: String,
            val missingDamage: Int
    )

    fun isValidSoFar(rowTest: Row, currentIndex: Int): Boolean {
        //println("   Validating: $rowTest ($currentIndex)")
        if (rowTest.missingDamage < 0 || rowTest.missingDamage > rowTest.unknownCount) {
            //println("      Failed checksum (missingDamage: ${rowTest.missingDamage}, unknownCount: ${rowTest.unknownCount})")
            return false
        }

        val testGroups = rowTest.row.substring(0, currentIndex+1).split('.').filterNot { it.isBlank() }
        return testGroups.map{ it.length }.zip(rowTest.groups).all { it.first == it.second }.also {
            //println("      Result: $it")
        }
    }

    fun testCombinations(row: Row, currentIndex: Int, cache: MutableMap<CombinationSignature, Long>): Long {
        //println("Row: $row ($currentIndex)")
        if ((row.missingDamage == 0 && row.unknownCount == 0) || currentIndex == row.row.length) {
            val isValid = isValidSoFar(row, row.row.length-1)
            //println("   Looks complete. Result: $isValid")
            return if (isValid) 1L else 0L
        }

        return when (row.row[currentIndex]) {
            '#' -> {
                testCombinations(row, currentIndex + 1, cache)
            }
            '.' -> {
                if (isValidSoFar(row, currentIndex)) {
                    val signature = CombinationSignature(row.row.substring(currentIndex), row.missingDamage)
                    if (cache.containsKey(signature)) {
                        cache[signature]!!
                    } else {
                        testCombinations(row, currentIndex + 1, cache).also {
                            cache[signature] = it
                        }
                    }
                } else {
                    0L
                }
            }
            '?' -> {
                listOf(
                        Row(row.row.replaceRange(currentIndex, currentIndex + 1, "#"), row.groups),
                        Row(row.row.replaceRange(currentIndex, currentIndex + 1, "."), row.groups)
                ).sumOf { comboRow ->
                    testCombinations(comboRow, currentIndex, cache)
                }
            }

            else -> throw IllegalStateException("Unexpected character in $row")
        }
    }

    fun part1Improved(input: List<String>): Long {
        val rows = input.map { line ->
            // Parse line
            val parts = line.split(' ')
            val row = parts[0]
            val groups = parts[1].split(',').map { it.toInt() }
            Row(row, groups)
        }

        val combos = rows.map { row ->
            val cache = mutableMapOf<CombinationSignature, Long>()
            testCombinations(row, 0, cache)
        }
        //println(combos)
        return combos.sum()
    }

    fun part2(input: List<String>): Long {
        val unfoldedInput = input.map { line ->
            // Parse line
            val parts = line.split(' ')
            val row = parts[0]
            val groups = parts[1].split(',').map { it.toInt() }

            // Unfold by duplicating
            val unfoldedRow = "$row?$row?$row?$row?$row"
            val unfoldedGroups = groups + groups + groups + groups + groups

            unfoldedRow + " " + unfoldedGroups.joinToString(",")
        }
        //unfoldedInput.forEach { println(it) }

        return part1Improved(unfoldedInput)
    }

    val testInput = readInput("Day12_test")
    //val testInput2 = readInput("Day12_test2")
    checkEquals(21L, part1(testInput))
    checkEquals(21L, part1Improved(testInput))
    checkEquals(525152L, part2(testInput))

    val input = readInput("Day12")
    //println(part1(input))
    println(part1Improved(input))
    println(part2(input))
}
