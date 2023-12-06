fun main() {
    data class Num(
            val number: Int,
            val startIndex: Int,
            val endIndex: Int, // inclusive
            val rowIndex: Int
    )

    data class Num2(
            val number: Int,
            val indexes: InclusiveRange<Int>,
            val rowIndex: Int
    )

    fun isSymbol(c: Char) : Boolean {
        return !c.isDigit() && c != '.'
    }

    fun isGear(c: Char) : Boolean {
        return c == '*'
    }

    fun part1(input: List<String>): Int {
        val colLength = input[0].length
        val rowLength = input.size

        val allNumbers: List<Num> = input.flatMapIndexed { i, line ->
            val delimiters = line.filter { !it.isDigit() }.toSet().toCharArray()
            var lineState = line
            val nums = line.split(*delimiters).filter{ it.isNotEmpty() }.map { n ->
                val startIndex = lineState.indexOf(n)
                val spacer = ".".repeat(n.length)
                lineState = lineState.replaceFirst(n, spacer)
                Num(n.toInt(), startIndex, startIndex+n.length-1, i)
            }
            nums
        }

        val partNumbers = allNumbers.filter { num ->
            if (num.rowIndex > 0) {
                // Check row before
                val str = input[num.rowIndex-1].substring(num.startIndex, num.endIndex+1)
                val symbols = str.filter { isSymbol(it) }
                if (symbols.isNotEmpty()) {
                    return@filter true
                }
            }
            if (num.rowIndex < rowLength-1) {
                // Check row after
                val str = input[num.rowIndex+1].substring(num.startIndex, num.endIndex+1)
                val symbols = str.filter { isSymbol(it) }
                if (symbols.isNotEmpty()) {
                    return@filter true
                }
            }
            if (num.startIndex > 0) {
                // Check column before
                if (isSymbol(input[num.rowIndex][num.startIndex-1])) {
                    return@filter true
                }

                if (num.rowIndex > 0 && isSymbol(input[num.rowIndex-1][num.startIndex-1])) {
                    return@filter true
                }
                if (num.rowIndex < rowLength-1 && isSymbol(input[num.rowIndex+1][num.startIndex-1])) {
                    return@filter true
                }
            }
            if (num.endIndex < colLength-1) {
                // Check column after
                if (isSymbol(input[num.rowIndex][num.endIndex+1])) {
                    return@filter true
                }

                if (num.rowIndex > 0 && isSymbol(input[num.rowIndex-1][num.endIndex+1])) {
                    return@filter true
                }
                if (num.rowIndex < rowLength-1 && isSymbol(input[num.rowIndex+1][num.endIndex+1])) {
                    return@filter true
                }
            }
            return@filter false
        }

        //println(partNumbers.map {it.number})
        return partNumbers.sumOf { it.number }
    }

    fun part2(input: List<String>): Int {
        val colLength = input[0].length
        val rowLength = input.size

        val allNumbers: List<Num2> = input.flatMapIndexed { i, line ->
            val delimiters = line.filter { !it.isDigit() }.toSet().toCharArray()
            var lineState = line
            val nums = line.split(*delimiters).filter{ it.isNotEmpty() }.map { n ->
                val startIndex = lineState.indexOf(n)
                val spacer = ".".repeat(n.length)
                lineState = lineState.replaceFirst(n, spacer)
                Num2(n.toInt(), InclusiveRange(startIndex, startIndex+n.length-1), i)
            }
            nums
        }

        val partNumbers = allNumbers.filter { num ->
            if (num.rowIndex > 0) {
                // Check row before
                val str = input[num.rowIndex-1].substring(num.indexes.x, num.indexes.y+1)
                val symbols = str.filter { isGear(it) }
                if (symbols.isNotEmpty()) {
                    return@filter true
                }
            }
            if (num.rowIndex < rowLength-1) {
                // Check row after
                val str = input[num.rowIndex+1].substring(num.indexes.x, num.indexes.y+1)
                val symbols = str.filter { isGear(it) }
                if (symbols.isNotEmpty()) {
                    return@filter true
                }
            }
            if (num.indexes.x > 0) {
                // Check column before
                if (isGear(input[num.rowIndex][num.indexes.x-1])) {
                    return@filter true
                }

                if (num.rowIndex > 0 && isGear(input[num.rowIndex-1][num.indexes.x-1])) {
                    return@filter true
                }
                if (num.rowIndex < rowLength-1 && isGear(input[num.rowIndex+1][num.indexes.x-1])) {
                    return@filter true
                }
            }
            if (num.indexes.y < colLength-1) {
                // Check column after
                if (isGear(input[num.rowIndex][num.indexes.y+1])) {
                    return@filter true
                }

                if (num.rowIndex > 0 && isGear(input[num.rowIndex-1][num.indexes.y+1])) {
                    return@filter true
                }
                if (num.rowIndex < rowLength-1 && isGear(input[num.rowIndex+1][num.indexes.y+1])) {
                    return@filter true
                }
            }
            return@filter false
        }

//        input.mapIndexed { i, line ->
//            var lineState = line.map {
//                if (isGear(it)) '*' else '.'
//            }.joinToString("")
//            val parts = partNumbers.filter { it.rowIndex == i }
//            parts.forEach {
//                lineState = lineState.replaceRange(it.indexes.x, it.indexes.y+1, it.number.toString())
//            }
//            println(lineState)
//        }
        val gears = input.flatMapIndexed { i, line ->
            line.mapIndexed{ j, c -> if(isGear(c)) Coordinates(i, j) else null }
        }.filterNotNull()

        val gearRatios: Map<Coordinates, List<Num2>> = gears.associate { gear ->
            val gearColRange = InclusiveRange(gear.col-1, gear.col+1)
            Pair(gear, partNumbers.filter { part ->
                if (part.rowIndex == gear.row-1 || part.rowIndex == gear.row || part.rowIndex == gear.row+1) {
                    return@filter part.indexes.overlaps(gearColRange)
                }
                return@filter false
            })
        }.filter { it.value.size == 2 }

        return gearRatios.toList().sumOf { (_, parts) ->
            parts[0].number * parts[1].number
        }
    }

    val testInput = readInput("Day03_test")
    check(part1(testInput) == 4361)
    check(part2(testInput) == 467835)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}