data class Hailstone2D(
    val x: Long,
    val y: Long,
    val mx: Long,
    val my: Long
) {
    companion object {
        fun fromString(input: String): Hailstone2D {
            val parts = input.split(' ', ',', '@').filter { it.isNotBlank() }.map { it.toLong() }
            checkEquals(6, parts.size)
            return Hailstone2D(parts[0], parts[1], parts[3], parts[4])//.also {println(it)}
        }
    }

    private val slope = my.toDouble() / mx.toDouble()

    fun intersects(other: Hailstone2D): Pair<Double, Double>? {
        if (this.slope == other.slope) {
            // Parallel lines
            return null
        }

        // Find intersection
        val intersectX = ((other.slope * other.x) - (this.slope * this.x) + this.y - other.y) / (other.slope - this.slope)
        val intersectY = (this.slope * (intersectX - this.x)) + this.y

        // Check if point is within bounds of rays (not in past)
        if (!this.isInBounds(intersectX, intersectY)) return null
        if (!other.isInBounds(intersectX, intersectY)) return null

        return Pair(intersectX, intersectY)
    }

    private fun isInBounds(testX: Double, testY: Double): Boolean {
        if (this.mx > 0 && testX < this.x) return false
        if (this.mx < 0 && testX > this.x) return false
        if (this.my > 0 && testY < this.y) return false
        if (this.my < 0 && testY > this.y) return false
        return true
    }
}

fun main() {
    fun part1(input: List<String>, boundaries: InclusiveRange<Long>): Int {
        val hailstones = input.map { Hailstone2D.fromString(it) }
        var intersectionCount = 0
        for(i in 0..<hailstones.size) {
            for(j in (i+1)..<hailstones.size) {
                val intersection = hailstones[i].intersects(hailstones[j])
                if (intersection != null) {
                    if (intersection.first >= boundaries.x
                            && intersection.first <= boundaries.y
                            && intersection.second >= boundaries.x
                            && intersection.second <= boundaries.y) {
                        intersectionCount += 1
                    }
                }
            }
        }

        return intersectionCount
    }

    //fun part2(input: List<String>, boundaries: InclusiveRange<Long>): Int {
        // https://jfmc.github.io/z3-play/
        // Online Z3 playground:

        // Plug in values for x1,y1,z1 mx1,my1,mz1 (and 2 and 3) from first three hailstones

        //(declare-const t1 Int)
        //(declare-const t2 Int)
        //(declare-const t3 Int)
        //(declare-const rx Int)
        //(declare-const ry Int)
        //(declare-const rz Int)
        //(declare-const rmx Int)
        //(declare-const rmy Int)
        //(declare-const rmz Int)
        //
        //(declare-const answer Int)
        //
        //(assert (= (+ (* mx1 t1) x1) (+ (* rmx t1) rx)))
        //(assert (= (+ (* my1 t1) y1) (+ (* rmy t1) ry)))
        //(assert (= (+ (* mz1 t1) z1) (+ (* rmz t1) rz)))
        //
        //(assert (= (+ (* mx2 t2) x2) (+ (* rmx t2) rx)))
        //(assert (= (+ (* my2 t2) y2) (+ (* rmy t2) ry)))
        //(assert (= (+ (* mz2 t2) z2) (+ (* rmz t2) rz)))
        //
        //(assert (= (+ (* mx3 t3) x3) (+ (* rmx t3) rx)))
        //(assert (= (+ (* my3 t3) y3) (+ (* rmy t3) ry)))
        //(assert (= (+ (* mz3 t3) z3) (+ (* rmz t3) rz)))
        //
        //(assert (= (+ (+ rx ry) rz) answer))
        //
        //(check-sat)
        //(get-model)
    //}

    val testInput = readInput("Day24_test")
    checkEquals(2, part1(testInput, InclusiveRange(7, 27)))

    val input = readInput("Day24")
    println(part1(input, InclusiveRange(200000000000000L, 400000000000000L)))
}
