import kotlin.system.measureTimeMillis

fun main() {
    fun readMap(input: List<String>, startIndex: Int, expectedMap: String): Map<InclusiveRange<Long>, Long> {
        check(input[startIndex].isEmpty())
        checkEquals(expectedMap, input[startIndex + 1])
        val seedToSoilMap = mutableMapOf<InclusiveRange<Long>, Long>()
        var i = startIndex + 2
        while(i < input.size && input[i].isNotEmpty()) {
            //println("Line $i: ${input[i]}")
            val lineParts = input[i].split(' ').map { it.toLong() }
            checkEquals(3, lineParts.size)
            val diff = lineParts[0] - lineParts[1]
            val sourceRange = InclusiveRange(lineParts[1], lineParts[1]+lineParts[2]-1)
            seedToSoilMap[sourceRange] = diff
            i += 1
        }
        return seedToSoilMap.toMap()
    }

    fun applyMap(source: Long, map: Map<InclusiveRange<Long>, Long>): Long {
        val key = map.keys.find { it.fullyContains(InclusiveRange(source, source)) }
        return if (key == null) {
            source
        } else {
            source + map[key]!!
        }
    }

    fun applyMap(source: Map<InclusiveRange<Long>, Long>, map: Map<InclusiveRange<Long>, Long>): Map<InclusiveRange<Long>, Long> {
        var newRanges = mutableSetOf<InclusiveRange<Long>>()
        source.keys.forEach { range ->
            val ranges = map.keys.filter { k -> k.overlaps(range) }
            if (ranges.isEmpty()) {
                newRanges.add(range)
            } else {
                val overlapped = ranges.map { range.intersect(it) }
                val remainingR1 = overlapped.flatMap { k -> range.remove(k){x,y -> x+y} }
                val remainingR2 = ranges.flatMap { k -> overlapped.flatMap { ok -> k.remove(ok){x,y->x+y} } }

                overlapped.forEach { newRanges.add(it) }
                remainingR1.forEach { newRanges.add(it) }
                remainingR2.forEach { newRanges.add(it) }
            }
        }
        map.keys.forEach { range ->
            val ranges = source.keys.filter { k -> k.overlaps(range) }
            if (ranges.isEmpty()) {
                newRanges.add(range)
            } else {
                val overlapped = ranges.map { range.intersect(it) }
                val remainingR1 = overlapped.flatMap { k -> range.remove(k){x,y -> x+y} }
                val remainingR2 = ranges.flatMap { k -> overlapped.flatMap { ok -> k.remove(ok){x,y->x+y} } }

                overlapped.forEach { newRanges.add(it) }
                remainingR1.forEach { newRanges.add(it) }
                remainingR2.forEach { newRanges.add(it) }
            }
        }

        newRanges = newRanges.filter { k ->
            val dupes = newRanges.filterNot { it == k }.filter { it.overlaps(k) }
            if (dupes.isNotEmpty()) {
                val myRangeSize = k.y - k.x
                val dupeRangeSize = dupes.minOf{ d -> d.y - d.x}
                check(myRangeSize != dupeRangeSize)
                if (myRangeSize > dupeRangeSize) {
                    return@filter false
                }
            }
            return@filter true
        }.toMutableSet()

        return newRanges.associateWith { range ->
            val inOriginalMap = source.entries.find { range.overlaps(it.key) }
            val inTransformMap = map.entries.find { range.overlaps(it.key) }

            if (inOriginalMap != null && inTransformMap != null) {
                inOriginalMap.value + inTransformMap.value
            } else if (inOriginalMap != null) {
                inOriginalMap.value
            } else {
                check(inTransformMap != null)
                inTransformMap.value
            }
        }
    }
    fun Map<InclusiveRange<Long>, Long>.applyMap(map: Map<InclusiveRange<Long>, Long>): Map<InclusiveRange<Long>, Long> {
        return applyMap(this, map)
    }

    fun part1(input: List<String>): Long {
        check(input[0].startsWith("seeds: "))
        val seeds = input[0].replace("seeds: ", "").split(' ').map { it.toLong() }
        var readIndex = 1

        val seedToSoilMap = readMap(input, readIndex, "seed-to-soil map:")
        readIndex += 2 + seedToSoilMap.size

        val soilToFertilizerMap = readMap(input, readIndex, "soil-to-fertilizer map:")
        readIndex += 2 + soilToFertilizerMap.size

        val fertilizerToWaterMap = readMap(input, readIndex, "fertilizer-to-water map:")
        readIndex += 2 + fertilizerToWaterMap.size

        val waterToLightMap = readMap(input, readIndex, "water-to-light map:")
        readIndex += 2 + waterToLightMap.size

        val lightToTemperatureMap = readMap(input, readIndex, "light-to-temperature map:")
        readIndex += 2 + lightToTemperatureMap.size

        val temperatureToHumidityMap = readMap(input, readIndex, "temperature-to-humidity map:")
        readIndex += 2 + temperatureToHumidityMap.size

        val humidityToLocationMap = readMap(input, readIndex, "humidity-to-location map:")
        readIndex += 2 + humidityToLocationMap.size
        checkEquals(input.size, readIndex)

        val locations = seeds
                .asSequence()
                .map { applyMap(it, seedToSoilMap) }
                .map { applyMap(it, soilToFertilizerMap) }
                .map { applyMap(it, fertilizerToWaterMap) }
                .map { applyMap(it, waterToLightMap) }
                .map { applyMap(it, lightToTemperatureMap) }
                .map { applyMap(it, temperatureToHumidityMap) }
                .map { applyMap(it, humidityToLocationMap) }
                .toList()

//        println("c1")
//        seedToSoilMap.applyMap(soilToFertilizerMap).forEach { println(it) }
//        println("fertilizerToWaterMap")
//        fertilizerToWaterMap.forEach {println(it)}
//        println("c2")
//        seedToSoilMap.applyMap(soilToFertilizerMap).applyMap(fertilizerToWaterMap).forEach { println(it) }
//
//        val compositeMap = seedToSoilMap
//                .applyMap(soilToFertilizerMap)
//                .applyMap(fertilizerToWaterMap)
////                .applyMap(waterToLightMap)
////                .applyMap(lightToTemperatureMap)
////                .applyMap(temperatureToHumidityMap)
////                .applyMap(humidityToLocationMap)
//        val l1 = seeds
//                .map { applyMap(it, seedToSoilMap) }
//                .map { applyMap(it, soilToFertilizerMap) }
//                .map { applyMap(it, fertilizerToWaterMap) }
////                .map { applyMap(it, waterToLightMap) }
////                .map { applyMap(it, lightToTemperatureMap) }
////                .map { applyMap(it, temperatureToHumidityMap) }
////                .map { applyMap(it, humidityToLocationMap) }
//        val l2 = seeds.map { applyMap(it, compositeMap) }
//        println(l1)
//        println(l2)
//        checkEquals(l1, l2)

        return locations.min()
    }

    fun part2(input: List<String>): Long {
        check(input[0].startsWith("seeds: "))
        val seeds = input[0].replace("seeds: ", "").split(' ').map { it.toLong() }
        var readIndex = 1

        val seedToSoilMap = readMap(input, readIndex, "seed-to-soil map:")
        readIndex += 2 + seedToSoilMap.size

        val soilToFertilizerMap = readMap(input, readIndex, "soil-to-fertilizer map:")
        readIndex += 2 + soilToFertilizerMap.size

        val fertilizerToWaterMap = readMap(input, readIndex, "fertilizer-to-water map:")
        readIndex += 2 + fertilizerToWaterMap.size

        val waterToLightMap = readMap(input, readIndex, "water-to-light map:")
        readIndex += 2 + waterToLightMap.size

        val lightToTemperatureMap = readMap(input, readIndex, "light-to-temperature map:")
        readIndex += 2 + lightToTemperatureMap.size

        val temperatureToHumidityMap = readMap(input, readIndex, "temperature-to-humidity map:")
        readIndex += 2 + temperatureToHumidityMap.size

        val humidityToLocationMap = readMap(input, readIndex, "humidity-to-location map:")
        readIndex += 2 + humidityToLocationMap.size
        checkEquals(input.size, readIndex)

        var min: Long = Long.MAX_VALUE
        //val seedRanges = seeds.chunked(2)
        //println(seedRanges)
        seeds.chunked(2).forEach { (start, length) ->
            (start..<start+length).forEach { seed ->
                val seedMin = applyMap(
                        applyMap(
                                applyMap(
                                        applyMap(
                                                applyMap(
                                                        applyMap(
                                                                applyMap(seed, seedToSoilMap),
                                                                soilToFertilizerMap
                                                        ),
                                                        fertilizerToWaterMap
                                                ),
                                                waterToLightMap
                                        ),
                                        lightToTemperatureMap
                                ),
                                temperatureToHumidityMap
                        ),
                        humidityToLocationMap
                )
                min = minOf(min, seedMin)
            }
        }
        return min
    }

    // New InclusiveRange tests
    val range1 = InclusiveRange<Long>(1, 30)
    checkEquals(listOf(InclusiveRange<Long>(11, 30)), range1.remove(InclusiveRange(-10, 10)) { x, y -> x+y })
    checkEquals(listOf(InclusiveRange<Long>(1, 19)), range1.remove(InclusiveRange(20, 40)) { x, y -> x+y })
    checkEquals(listOf(), range1.remove(InclusiveRange(1, 30)) { x, y -> x+y })
    checkEquals(listOf(), range1.remove(InclusiveRange(-10, 40)) { x, y -> x+y })
    checkEquals(listOf(range1), range1.remove(InclusiveRange(40, 50)) { x, y -> x+y })
    checkEquals(listOf(InclusiveRange<Long>(11, 30)), range1.remove(InclusiveRange(1, 10)) { x, y -> x+y })
    checkEquals(listOf(InclusiveRange<Long>(1, 19)), range1.remove(InclusiveRange(20, 30)) { x, y -> x+y })
    checkEquals(listOf(InclusiveRange<Long>(1, 9), InclusiveRange<Long>(21, 30)), range1.remove(InclusiveRange(10, 20)) { x, y -> x+y })

    checkEquals(range1, range1.intersect(InclusiveRange(-10, 40)))
    checkEquals(InclusiveRange<Long>(10, 20), range1.intersect(InclusiveRange(10, 20)))
    checkEquals(InclusiveRange<Long>(1, 20), range1.intersect(InclusiveRange(-5, 20)))
    checkEquals(InclusiveRange<Long>(20, 30), range1.intersect(InclusiveRange(20, 40)))
    try {
        range1.intersect(InclusiveRange(40, 50))
        throw UnsupportedOperationException()
    } catch (_: IllegalStateException) {
        // Expected failure
    }



    val testInput = readInput("Day05_test")
    checkEquals(35L, part1(testInput))
    checkEquals(46L, part2(testInput))

    val input = readInput("Day05")
    println(measureTimeMillis {
        println(part1(input))
    })
    println(measureTimeMillis {
        println(part2(input))
    })
}
