fun main() {
    fun part1(input: List<String>, limits: Map<String, Int>): Int {
        val games = input.map{ it.replace("Game ", "") }
            .associate { line ->
                val split = line.split(':')
                val key = split[0].toInt()
                val draws = split[1].trim()
                    .split(";")
                    .map{draw ->
                        val cubes = draw.trim().split(",").map{
                            CubeDraw(it.trim().split(' '))
                        }
                        cubes
                    }
                Pair(key, draws)
        }
        val validGames = games.filter { game ->
            val cubset = game.value
            //println("${game.key}) $cubset")
            cubset.map { draw ->
                //println("  $draw")
                draw.map { cubes ->
                    //println("    ${limits[cubes.color]!!} >= ${cubes.number}) ==> ${limits[cubes.color]!! >= cubes.number}")
                    limits[cubes.color]!! >= cubes.number
                }.all { it }
            }.all { it }
        }
        //println(validGames.keys)
        return validGames.keys.sum()
    }

    fun part2(input: List<String>): Int {
        val games = input.map{ it.replace("Game ", "") }
            .associate { line ->
                val split = line.split(':')
                val key = split[0].toInt()
                val draws = split[1].trim()
                        .split(";")
                        .map{draw ->
                            val cubes = draw.trim().split(",").map{
                                CubeDraw(it.trim().split(' '))
                            }
                            cubes
                        }
                Pair(key, draws)
            }
        val product = games.map { (id, game) ->
            val maxRed = game.maxOfOrNull { draw -> draw.firstOrNull { it.color == "red" }?.number ?: 0 } ?: 1
            val maxGreen = game.maxOfOrNull { draw -> draw.firstOrNull { it.color == "green" }?.number ?: 0 } ?: 1
            val maxBlue = game.maxOfOrNull { draw -> draw.firstOrNull { it.color == "blue" }?.number ?: 0 } ?: 1
            maxRed * maxBlue * maxGreen
        }
        //println("$product  ->  ${product.sum()}")
        return product.sum()
    }

    //12 red cubes, 13 green cubes, and 14 blue cubes
    val limits = mapOf(
            Pair("red", 12),
            Pair("green", 13),
            Pair("blue", 14)
    )

    val testInput = readInput("Day02_test")
    val testInput2 = readInput("Day02_test2")
    check(part1(testInput, limits) == 8)
    check(part2(testInput2) == 2286)

    val input = readInput("Day02")
    println(part1(input, limits))
    println(part2(input))
}

data class CubeDraw(
    val number: Int,
    val color: String
) {
    constructor(input: List<String>) : this(input[0].toInt(), input[1])
}
