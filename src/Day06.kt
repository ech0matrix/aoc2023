fun main() {
    fun part1(input: List<String>): Int {
        val times = input[0].split(" ").map { it.trim() }.filter { it.isNotEmpty() }.drop(1).map { it.toInt() }
        val distances = input[1].split(" ").map { it.trim() }.filter { it.isNotEmpty() }.drop(1).map { it.toInt() }
        val races = times.zip(distances)

        return races.map { (time, distance) ->
            // Calculate possible wins for each race
            (0..time).count { speed ->
                // Calculate distance for each speed
                val remainingTime = time - speed
                val distanceTraveled = speed * remainingTime
                distanceTraveled > distance
            }
        }.reduce(Int::times) // Return product of all possibilities
    }

    fun part2(input: List<String>): Int {
        val time = input[0].replace(" ", "").split(":")[1].toLong()
        val distance = input[1].replace(" ", "").split(":")[1].toLong()

        return (0..time).count { speed ->
            // Calculate distance for each speed
            val remainingTime = time - speed
            val distanceTraveled = speed * remainingTime
            distanceTraveled > distance
        }
    }

    val testInput = readInput("Day06_test")
    check(part1(testInput) == 288)
    check(part2(testInput) == 71503)

    val input = readInput("Day06")
    println(part1(input))
    println(part2(input))
}
