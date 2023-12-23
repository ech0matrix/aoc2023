data class Brick(
    val x: InclusiveRange<Int>,
    val y: InclusiveRange<Int>,
    val z: InclusiveRange<Int>,
    val id: String
) {
    fun overlaps(other: Brick): Boolean {
        return (this.x.overlaps(other.x))
                && (this.y.overlaps(other.y))
                && (this.z.overlaps(other.z))
    }
}

fun main() {
    fun dropBricks(bricks: MutableList<Brick>): Pair<Boolean, Set<String>> {
        var dropBricks = false
        var bricksDropped = mutableSetOf<String>()
        for(i in bricks.indices) {
            val brick = bricks[i]
            val dropped = brick.copy(z = InclusiveRange(brick.z.x-1, brick.z.y-1))
            val isSafeToMove = (dropped.z.x > 0) && bricks.filter { it != brick }.none { it.overlaps(dropped) }
            if (isSafeToMove) {
                bricks[i] = dropped
                dropBricks = true
                bricksDropped.add(dropped.id)
            }
        }
        return Pair(dropBricks, bricksDropped.toSet())
    }

    fun parseBricks(input: List<String>): MutableList<Brick> {
        return input.map { line ->
            val parts = line.split(",", "~").filter{ it.isNotBlank() }.map{ it.toInt() }
            checkEquals(6, parts.size)
            Brick(
                    InclusiveRange(parts[0], parts[3]),
                    InclusiveRange(parts[1], parts[4]),
                    InclusiveRange(parts[2], parts[5]),
                    line
            )
        }.toMutableList()
    }

    fun part1(input: List<String>): Int {
        val bricks = parseBricks(input)

        var dropBricks = true
        while(dropBricks) {
            dropBricks = dropBricks(bricks).first
        }

        return bricks.count { brick ->
            val remainingBricks = bricks.filter { it != brick }
            !(dropBricks(remainingBricks.toMutableList()).first)
        }
    }

    fun part2(input: List<String>): Int {
        val bricks = parseBricks(input)

        var dropBricks = true
        while(dropBricks) {
            dropBricks = dropBricks(bricks).first
        }

        return bricks.sumOf { brick ->
            val remainingBricks = bricks.filter { it != brick }.toMutableList()
            val droppedBricks = mutableSetOf<String>()
            dropBricks = true
            while(dropBricks) {
                val result = dropBricks(remainingBricks)
                dropBricks = result.first
                droppedBricks.addAll(result.second)
            }
            droppedBricks.size
        }
    }

    val testInput = readInput("Day22_test")
    timeIt {
        checkEquals(5, part1(testInput))
    }
    timeIt {
        checkEquals(7, part2(testInput))
    }

    val input = readInput("Day22")
    timeIt {
        println(part1(input))
    }
    timeIt {
        println(part2(input))
    }
}
