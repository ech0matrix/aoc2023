import java.util.*

enum class Operation {
    GREATER_THAN,
    LESS_THAN;

    companion object {
        fun parse(input: Char): Operation {
            return when(input) {
                '>' -> GREATER_THAN
                '<' -> LESS_THAN
                else -> throw IllegalStateException("Unknown operation: $input")
            }
        }
    }
}

data class Comparison(
    val category: String,
    val operation: Operation,
    val amount: Int
) {
    companion object {
        fun parse(input: String): Comparison {
            // Example 1: m>2090
            // Example 2: m<1801
            return Comparison(
                input[0].toString(),
                Operation.parse(input[1]),
                input.drop(2).toInt()
            )
        }
    }
}

data class Rule(
    val result: String,
    val comparison: Comparison?
) {
    companion object {
        // Example 1: a<2006:qkq
        // Example 2: rfg
        fun parse(input: String): Rule {
            val parts = input.split(':').filter { it.isNotBlank() }
            check(parts.size == 1 || parts.size == 2)
            val result = parts.last()
            val comparison = if(parts.size == 2) Comparison.parse(parts.first()) else null
            return Rule(result, comparison)
        }
    }
}

data class Workflow(
    val label: String,
    val steps: List<Rule>
) {
    companion object {
        fun parse(input: String): Workflow {
            // Example: px{a<2006:qkq,m>2090:A,rfg}
            val parts = input.split('{', '}').filter { it.isNotBlank() }
            check(parts.size == 2)
            val label = parts[0]
            val rawSteps = parts[1].split(',')
            return Workflow(label, rawSteps.map { Rule.parse(it) })
        }
    }
}

data class FactoryPart(
    val rating: Map<String, Int>
) {
    companion object {
        fun parse(input: String): FactoryPart {
            // Example: {x=787,m=2655,a=1222,s=2876}
            val rawRatings = input.split('{', '}', ',').filter { it.isNotBlank() }
            check(rawRatings.size == 4)
            val ratings = rawRatings.associate { rating ->
                val parts = rating.split('=')
                check(parts.size == 2)
                check(parts[0].length == 1)
                Pair(parts[0], parts[1].toInt())
            }
            return FactoryPart(ratings)
        }
    }
}

typealias Categories = Map<String, InclusiveRange<Int>>

fun main() {
    fun isRuleMatch(factoryPart: FactoryPart, currentWorkflow: Workflow, ruleIndex: Int): Boolean {
        val currentRule = currentWorkflow.steps[ruleIndex]
        return if (currentRule.comparison == null) {
            true
        } else {
            val leftSide = factoryPart.rating[currentRule.comparison.category]!!
            val rightSide = currentRule.comparison.amount
            when(currentRule.comparison.operation) {
                Operation.GREATER_THAN -> leftSide > rightSide
                Operation.LESS_THAN -> leftSide < rightSide
            }
        }
    }

    fun isApproved(factoryPart: FactoryPart, startWorkflow: String, workflows: Map<String, Workflow>): Boolean {
        return when (startWorkflow) {
            "A" -> true
            "R" -> false
            else -> {
                val currentWorkflow = workflows[startWorkflow]!!
                var ruleIndex = 0
                while (!isRuleMatch(factoryPart, currentWorkflow, ruleIndex)) {
                    ruleIndex += 1
                }
                val matchedRule = currentWorkflow.steps[ruleIndex]
                isApproved(factoryPart, matchedRule.result, workflows)
            }
        }
    }

    fun parseInput(input: List<String>): Pair<Map<String, Workflow>, List<FactoryPart>> {
        val splitInputIndex = input.indexOfFirst { it.isBlank() }
        val workflows = input.subList(0, splitInputIndex).map { Workflow.parse(it) }.associateBy { it.label }
        val factoryParts = input.subList(splitInputIndex+1, input.size).map { FactoryPart.parse(it) }
        return Pair(workflows, factoryParts)
    }

    fun part1(input: List<String>): Int {
        val (workflows, factoryParts) = parseInput(input)

        val approvedParts = factoryParts.filter { isApproved(it, "in", workflows) }

        return approvedParts.sumOf { part ->
            part.rating.values.sum()
        }
    }

    data class TestState(
            val ranges: Categories,
            val currentWorkflow: String,
            val currentRuleIndex: Int
    )

    fun part2(input: List<String>): Long {
        val (workflows, _) = parseInput(input)

        val fullRange = InclusiveRange(1, 4000)
        val allPossibilities = mapOf(
                "x" to fullRange,
                "m" to fullRange,
                "a" to fullRange,
                "s" to fullRange
        )

        val approvedRanges = mutableListOf<Categories>()
        val testStates: Queue<TestState> = LinkedList()
        testStates.add(TestState(
                allPossibilities,
                "in",
                0
        ))

        while(testStates.isNotEmpty()) {
            val currentTest = testStates.remove()
            when (currentTest.currentWorkflow) {
                "A" -> approvedRanges.add(currentTest.ranges)
                "R" -> {}
                else -> {
                    val currentWorkflow = workflows[currentTest.currentWorkflow]!!
                    val currentRule = currentWorkflow.steps[currentTest.currentRuleIndex]
                    var positiveMatch: Categories? = null
                    var negativeMatch: Categories? = null
                    if (currentRule.comparison == null) {
                        positiveMatch = currentTest.ranges
                    } else {
                        val category = currentRule.comparison.category
                        val rightSide = currentRule.comparison.amount
                        val operation = currentRule.comparison.operation

                        val positiveMatchRange = when(operation) {
                            Operation.GREATER_THAN -> InclusiveRange(rightSide+1, fullRange.y)
                            Operation.LESS_THAN -> InclusiveRange(fullRange.x, rightSide-1)
                        }
                        val negativeMatchRange = when(operation) {
                            Operation.GREATER_THAN -> InclusiveRange(fullRange.x, rightSide)
                            Operation.LESS_THAN -> InclusiveRange(rightSide, fullRange.y)
                        }

                        if (currentTest.ranges[category]!!.overlaps(positiveMatchRange)) {
                            positiveMatch = currentTest.ranges.mapValues { (k, v) ->
                                if (k == category) {
                                    currentTest.ranges[category]!!.intersect(positiveMatchRange)
                                } else {
                                    v
                                }
                            }
                        }
                        if (currentTest.ranges[category]!!.overlaps(negativeMatchRange)) {
                            negativeMatch = currentTest.ranges.mapValues { (k, v) ->
                                if (k == category) {
                                    currentTest.ranges[category]!!.intersect(negativeMatchRange)
                                } else {
                                    v
                                }
                            }
                        }
                    }

                    if (positiveMatch != null) {
                        val nextWorkflow = currentRule.result
                        testStates.add(TestState(
                            positiveMatch,
                            nextWorkflow,
                            0
                        ))
                    }
                    if (negativeMatch != null) {
                        testStates.add(TestState(
                            negativeMatch,
                            currentTest.currentWorkflow,
                            currentTest.currentRuleIndex + 1
                        ))
                    }
                }
            }
        }

        return approvedRanges.sumOf { range ->
            val x = range["x"]!!.y - range["x"]!!.x + 1
            val m = range["m"]!!.y - range["m"]!!.x + 1
            val a = range["a"]!!.y - range["a"]!!.x + 1
            val s = range["s"]!!.y - range["s"]!!.x + 1
            x.toLong() * m.toLong() * a.toLong() * s.toLong()
        }
    }

    val testInput = readInput("Day19_test")
    checkEquals(19114, part1(testInput))
    checkEquals(167409079868000L, part2(testInput))

    val input = readInput("Day19")
    println(part1(input))
    println(part2(input))
}
