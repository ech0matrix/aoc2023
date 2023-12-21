import java.util.*

enum class Pulse {
    LOW,
    HIGH;

    override fun toString(): String {
        return super.toString().lowercase(Locale.getDefault())
    }
}

abstract class Module(
        val label: String
) {
    val outputs = mutableListOf<Module>()

    abstract fun receiveSignal(pulse: Pulse, source: String): List<Signal>
}

data class Signal(
    val sourceLabel: String,
    val destination: Module,
    val pulse: Pulse
) {
    override fun toString(): String {
        return "$sourceLabel -$pulse-> ${destination.label}"
    }
}

data class SignalCounts(
    val lowPulseCount: Long,
    val highPulseCount: Long,
) {
    val totalCount = lowPulseCount + highPulseCount
    val product = lowPulseCount * highPulseCount
}

fun main() {

    class Broadcaster : Module("broadcaster") {
        override fun receiveSignal(pulse: Pulse, source: String): List<Signal> {
            return outputs.map { output ->
                Signal(label, output, pulse)
            }
        }
    }

    class FlipFlop(label: String) : Module(label) {
        var state = false

        override fun receiveSignal(pulse: Pulse, source: String): List<Signal> {
            return if(pulse == Pulse.HIGH) {
                listOf()
            } else {
                state = !state
                val sendPulse = when(state) {
                    true -> Pulse.HIGH
                    false -> Pulse.LOW
                }
                outputs.map { output ->
                    Signal(label, output, sendPulse)
                }
            }
        }
    }

    class Conjunction(label: String) : Module(label) {
        val inputStates = mutableMapOf<String, Pulse>()

        override fun receiveSignal(pulse: Pulse, source: String): List<Signal> {
            inputStates[source] = pulse
            // Then, if it remembers high pulses for all inputs, it sends a low pulse; otherwise, it sends a high pulse.
            val sendPulse = if(inputStates.values.all { it == Pulse.HIGH }) Pulse.LOW else Pulse.HIGH

            return outputs.map { output ->
                Signal(label, output, sendPulse)
            }
        }
    }

    class DebugModule(label: String) : Module(label) {
        override fun receiveSignal(pulse: Pulse, source: String): List<Signal> {
            return listOf()
        }
    }

    fun labelName(label: String): String {
        return if(label[0] == '%' || label[0] == '&') label.drop(1) else label
    }

    fun setupModules(input: List<String>): MutableMap<String, Module> {
        val inputParts = input.map { it.split(" -> ", ", ") }

        val modules = mutableMapOf<String, Module>()

        // Create modules
        inputParts.forEach { inputPart ->
            val label = inputPart[0]
            val module = if (label == "broadcaster") {
                Broadcaster()
            } else if (label[0] == '%') {
                FlipFlop(labelName(label))
            } else if (label[0] == '&') {
                Conjunction(labelName(label))
            } else {
                throw IllegalStateException("Unknown module: $label")
            }
            modules[labelName(label)] = module
        }

        // Wire outputs
        inputParts.forEach { inputPart ->
            val label = labelName(inputPart[0])
            val outputs = inputPart.drop(1)
            val module = modules[label]!!
            outputs.forEach { outputLabel ->
                val outputModule = modules[outputLabel] ?: run{
                    println("Creating DebugModule: $outputLabel")
                    DebugModule(outputLabel).also {
                        modules[outputLabel] = it
                    }
                }
                module.outputs.add(outputModule)
            }
        }

        // Setup input state on conjunctions
        val conjunctionLabels = modules.filterValues { it is Conjunction }.values.map { it.label }
        inputParts.forEach { inputPart ->
            val label = labelName(inputPart[0])
            val conjunctionOutputs = inputPart.drop(1)
                    .filter { conjunctionLabels.contains(it) }
                    .map { modules[it]!! as Conjunction }
            conjunctionOutputs.forEach { conjunction ->
                conjunction.inputStates[label] = Pulse.LOW
            }
        }

        return modules
    }

    fun part1(input: List<String>, buttonPresses: Int): SignalCounts {
        val modules = setupModules(input)

        var lowPulseCount = 0L
        var highPulseCount = 0L
        repeat(buttonPresses) {
            var signals = listOf(Signal("button", modules["broadcaster"]!!, Pulse.LOW))
            while(signals.isNotEmpty()) {
                val nextSignals = mutableListOf<Signal>()
                signals.forEach { signal ->
                    when(signal.pulse) {
                        Pulse.LOW -> lowPulseCount += 1
                        Pulse.HIGH -> highPulseCount += 1
                    }
//                    println(signal)
//                    println("  lowCount: $lowPulseCount, highCount: $highPulseCount")
                    nextSignals.addAll(
                        signal.destination.receiveSignal(signal.pulse, signal.sourceLabel)
                    )
                }
                signals = nextSignals
            }
        }

        return SignalCounts(lowPulseCount, highPulseCount)
    }

    fun part2(input: List<String>): Long {
        val modules = setupModules(input)

        val rx = modules["rx"]!!
        val inputToRx = modules.values.first { it.outputs.contains(rx) }
        check(inputToRx is Conjunction)

        val inputsToConjunction = inputToRx.inputStates.keys.associateWith { -1L }.toMutableMap()

        var buttonPresses = 0L
        var rxSignaled = false
        while(!rxSignaled) {
            buttonPresses += 1L
            var signals = listOf(Signal("button", modules["broadcaster"]!!, Pulse.LOW))
            while(signals.isNotEmpty()) {
                val nextSignals = mutableListOf<Signal>()
                signals.forEach { signal ->
                    if (signal.destination.label == "rx" && signal.pulse == Pulse.LOW) {
                        rxSignaled = true
                    }
                    if (signal.destination.label == inputToRx.label && signal.pulse == Pulse.HIGH) {
//                        println(signal)
//                        println("  Button presses: $buttonPresses")
                        if (inputsToConjunction[signal.sourceLabel]!! == -1L) {
                            inputsToConjunction[signal.sourceLabel] = buttonPresses
                            if (inputsToConjunction.none { it.value == -1L }) {
                                var conjunctionCycles = 1L
                                inputsToConjunction.forEach { i ->
                                    conjunctionCycles *= i.value
                                }
                                return conjunctionCycles
                            }
                        }
                    }
                    nextSignals.addAll(
                            signal.destination.receiveSignal(signal.pulse, signal.sourceLabel)
                    )
                }
                signals = nextSignals
            }
        }

        return buttonPresses
    }

    val testInput = readInput("Day20_test")
    val testInput2 = readInput("Day20_test2")
    checkEquals(12L, part1(testInput, 1).totalCount)
    checkEquals(28L, part1(testInput2, 4).totalCount)
    checkEquals(32000000L, part1(testInput, 1000).product)
    checkEquals(11687500L, part1(testInput2, 1000).product)

    val input = readInput("Day20")
    println(part1(input, 1000).product)
    println(part2(input))
}
