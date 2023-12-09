fun main() {
    fun predictNext(nums: List<Long>): Long {
        if (nums.all{ it == 0L }) {
            return 0L
        }

        val diffs = mutableListOf<Long>()
        for(i in 0 .. nums.size-2) {
            diffs.add(nums[i+1] - nums[i])
        }
        checkEquals(nums.size-1, diffs.size)
        val nextDiff = predictNext(diffs)
        return nums.last() + nextDiff
    }

    fun predictPrevious(nums: List<Long>): Long {
        if (nums.all{ it == 0L }) {
            return 0L
        }

        val diffs = mutableListOf<Long>()
        for(i in 0 .. nums.size-2) {
            diffs.add(nums[i+1] - nums[i])
        }
        checkEquals(nums.size-1, diffs.size)
        val previousDiff = predictPrevious(diffs)
        return nums.first() - previousDiff
    }

    fun parseHistories(input: List<String>): List<List<Long>> {
        return input.map { line ->
            line.split(" ").map { it.toLong() }
        }
    }

    fun part1(input: List<String>): Long {
        val histories = parseHistories(input)
        return histories.sumOf { predictNext(it) }
    }

    fun part2(input: List<String>): Long {
        val histories = parseHistories(input)
        return histories.sumOf { predictPrevious(it) }
    }

    val testInput = readInput("Day09_test")
    checkEquals(114L, part1(testInput))
    checkEquals(2L, part2(testInput))

    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}
