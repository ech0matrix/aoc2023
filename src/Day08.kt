import java.lang.IllegalStateException

fun main() {
    fun parseInput(input: List<String>): Pair<String, Map<String, Pair<String, String>>> {
        val instructions = input[0]
        val nodes = input
                .drop(2)
                .map { it.replace(" = (", " ").replace(", ", " ").replace(")", "") }
                .associate { line ->
                    val parts = line.split(" ")
                    Pair(parts[0], Pair(parts[1], parts[2]))
                }
        return Pair(instructions, nodes)
    }

    fun part1(input: List<String>): Int {
        val (instructions, nodes) = parseInput(input)

        var currentNode = "AAA"
        val targetNode = "ZZZ"
        var stepCount = 0
        while(currentNode != targetNode) {
            val step = instructions[stepCount % instructions.length]
            stepCount += 1
            currentNode = when (step) {
                'L' -> nodes[currentNode]!!.first
                'R' -> nodes[currentNode]!!.second
                else -> throw IllegalStateException("Unknown instruction: $step")
            }
        }

        return stepCount
    }

    fun gcd(x: Long, y: Long): Long {
        return if (y == 0L) x else gcd(y, x % y)
    }

//    public static int gcd(int... numbers) {
//        return Arrays.stream(numbers).reduce(0, (x, y) -> gcd(x, y));
//    }

    fun lcm(nums: List<Long>): Long {
        return nums.reduce{ x, y -> x * (y / gcd(x, y))}
    }

    fun part2(input: List<String>): Long {
        val (instructions, nodes) = parseInput(input)

//        var currentNodes = nodes.keys.filter { it.last() == 'A' }
//        var stepCount = 0L
//        while(!currentNodes.all { it.last() == 'Z' }) {
//            val step = instructions[(stepCount % instructions.length).toInt()]
//            stepCount += 1
//            currentNodes = when (step) {
//                'L' -> currentNodes.map { nodes[it]!!.first }
//                'R' -> currentNodes.map { nodes[it]!!.second }
//                else -> throw IllegalStateException("Unknown instruction: $step")
//            }
//        }

        val startingNodes = nodes.keys.filter { it.last() == 'A' }
        val solveCounts = startingNodes.map { node ->
            var currentNode = node
            var stepCount = 0L
            while(currentNode.last() != 'Z') {
                val step = instructions[(stepCount % instructions.length).toInt()]
                stepCount += 1
                currentNode = when (step) {
                    'L' -> nodes[currentNode]!!.first
                    'R' -> nodes[currentNode]!!.second
                    else -> throw IllegalStateException("Unknown instruction: $step")
                }
            }
            stepCount
        }

        return lcm(solveCounts)
    }

    checkEquals(2, part1(readInput("Day08_test")))
    checkEquals(6, part1(readInput("Day08_test2")))
    checkEquals(6L, part2(readInput("Day08_test3")))

    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}
