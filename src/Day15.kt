fun main() {
    fun hash(str: String): Int {
        var hash = 0
        str.forEach { c ->
            hash += c.code
            hash *= 17
            hash %= 256
        }
        return hash
    }

    fun part1(input: List<String>): Int {
        checkEquals(1, input.size)
        val sequence = input[0].split(",")
        return sequence.sumOf { hash(it) }
    }

    fun parseStep(str: String): Pair<String, Int?> {
        val parts = str.split('=', '-').filter { it.isNotBlank() }
        val label = parts[0]
        val amount = if (parts.size > 1) parts[1].toInt() else null
        return Pair(label, amount)
    }

    data class Lens(
            val label: String,
            val focus: Int
    )

    fun part2(input: List<String>): Int {
        checkEquals(1, input.size)
        val sequence = input[0].split(",")
        val boxes = (0..255).associateWith { mutableListOf<Lens>() }

        sequence.forEach { step ->
            val (label, focus) = parseStep(step)
            val hash = hash(label)
            val previous = boxes[hash]!!.find { it.label == label }
            if (focus == null && previous != null) {
                boxes[hash]!!.remove(previous)
            } else if (focus != null && previous == null) {
                boxes[hash]!!.add(Lens(label, focus))
            } else if (focus != null && previous != null) {
                boxes[hash]!!.replaceAll { if(it == previous) Lens(label, focus) else it }
            } else if (focus != null){
                throw IllegalStateException(step)
            }
        }

        boxes.onEachIndexed { index, box ->
            if (box.value.isNotEmpty()) {
                checkEquals(box.key, index)
                print("Box $index: ")
                box.value.forEach { lens ->
                    print("[${lens.label} ${lens.focus}] ")
                }
                println()
            }
        }

        return boxes.keys.sumOf { box ->
            var boxNum = 1 + box
            val lenses = boxes[box]!!
            var lensIndex = 0
            lenses.sumOf { lens ->
                lensIndex += 1
                val lensSlot = lensIndex
                lensSlot * lens.focus * boxNum
            }
        }
    }

    val test1 = parseStep("rn=1")
    checkEquals("rn", test1.first)
    checkEquals(1, test1.second)
    val test2 = parseStep("cm-")
    checkEquals("cm", test2.first)
    checkEquals(null, test2.second)

    val testInput = readInput("Day15_test")
    checkEquals(1320, part1(testInput))
    checkEquals(145, part2(testInput))

    val input = readInput("Day15")
    println(part1(input))
    println(part2(input))
}
