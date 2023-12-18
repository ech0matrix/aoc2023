import java.io.File
import java.lang.IllegalStateException
import java.math.BigInteger
import java.security.MessageDigest
import java.util.Date
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

data class LongCoordinates(
        val row: Long,
        val col: Long
) {
    fun add(other: LongCoordinates): LongCoordinates {
        return LongCoordinates(this.row + other.row, this.col + other.col)
    }

    fun manhattanDistance(other: LongCoordinates): Long {
        return abs(this.row - other.row) + abs(this.col - other.col)
    }
}

//data class InclusiveRange(
//        val x: Int,
//        val y: Int
//) {
//    fun fullyContains(other: InclusiveRange): Boolean {
//        return this.x <= other.x && this.y >= other.y
//    }
//
//    fun overlaps(other: InclusiveRange): Boolean {
//        return (this.x >= other.x && this.x <= other.y)
//                || (this.y >= other.x && this.y <= other.y)
//                || (other.x >= this.x && other.x <= this.y)
//                || (other.y >= this.x && other.y <= this.y)
//    }
//
//    fun merge(other: InclusiveRange): InclusiveRange {
//        check(overlaps(other))
//        return InclusiveRange(minOf(x, other.x), maxOf(y, other.y))
//    }
//}

data class InclusiveRange<T : Comparable<T>>(
        val x: T,
        val y: T
) {
    fun fullyContains(other: InclusiveRange<T>): Boolean {
        return this.x <= other.x && this.y >= other.y
    }

    fun overlaps(other: InclusiveRange<T>): Boolean {
        return (this.x >= other.x && this.x <= other.y)
                || (this.y >= other.x && this.y <= other.y)
                || (other.x >= this.x && other.x <= this.y)
                || (other.y >= this.x && other.y <= this.y)
    }

    fun merge(other: InclusiveRange<T>): InclusiveRange<T> {
        check(overlaps(other))
        return InclusiveRange(minOf(x, other.x), maxOf(y, other.y))
    }

    fun intersect(other: InclusiveRange<T>): InclusiveRange<T> {
        if (this.fullyContains(other)) {
            return other
        }
        if (other.fullyContains(this)) {
            return this
        }

        check(overlaps(other))
        return InclusiveRange(maxOf(x, other.x), minOf(y, other.y))
    }

    fun remove(other: InclusiveRange<T>, incrementer: (T, Int) -> T): List<InclusiveRange<T>> {
        if (!overlaps(other)) {
            return listOf(this)
        }
        if (other.fullyContains(this)) {
            return listOf()
        }
        if (fullyContains(other)) {
            if (x == other.x) {
                return listOf(InclusiveRange(incrementer(other.y, 1), y))
            }
            if (y == other.y) {
                return listOf(InclusiveRange(x, incrementer(other.x, -1)))
            }
            return listOf(
                InclusiveRange(x, incrementer(other.x, -1)),
                InclusiveRange(incrementer(other.y, 1), y)
            )
        }
        if (x < other.x) {
            return listOf(InclusiveRange(x, incrementer(other.x, -1)))
        }
        check(y > other.y)
        return listOf(InclusiveRange(incrementer(other.y, 1), y))
    }
}

fun <T> checkEquals(expected: T, actual: T) {
    if (expected != actual) {
        throw IllegalStateException("Expected value '$expected', actual value '$actual'")
    }
}

fun <T> timeIt(function: () -> T): T {
    val start = Date().time
    val result = function()
    val end = Date().time
    println("Time: ${end-start} ms")
    return result
}