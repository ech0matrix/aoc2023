import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.math.abs

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt")
    .readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)


data class Coordinates(
        val row: Int,
        val col: Int
) {
    fun add(other: Coordinates): Coordinates {
        return Coordinates(this.row + other.row, this.col + other.col)
    }

    fun manhattanDistance(other: Coordinates): Int {
        return abs(this.row - other.row) + abs(this.col - other.col)
    }
}

data class InclusiveRange(
        val x: Int,
        val y: Int
) {
    fun fullyContains(other: InclusiveRange): Boolean {
        return this.x <= other.x && this.y >= other.y
    }

    fun overlaps(other: InclusiveRange): Boolean {
        return (this.x >= other.x && this.x <= other.y)
                || (this.y >= other.x && this.y <= other.y)
                || (other.x >= this.x && other.x <= this.y)
                || (other.y >= this.x && other.y <= this.y)
    }

    fun merge(other: InclusiveRange): InclusiveRange {
        check(overlaps(other))
        return InclusiveRange(minOf(x, other.x), maxOf(y, other.y))
    }
}
