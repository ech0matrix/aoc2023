fun main() {
    fun addEdge(node1: String, node2: String, nodes: MutableMap<String, MutableSet<String>>) {
        if(!nodes.containsKey(node1)) {
            nodes[node1] = mutableSetOf()
        }
        if(!nodes.containsKey(node2)) {
            nodes[node2] = mutableSetOf()
        }

        nodes[node1]!!.add(node2)
        nodes[node2]!!.add(node1)
    }

    fun breakEdge(node1: String, node2: String, nodes: MutableMap<String, MutableSet<String>>) {
        nodes[node1]!!.remove(node2)
        nodes[node2]!!.remove(node1)
    }

    fun walkGraph(startNode: String, nodes: MutableMap<String, MutableSet<String>>): MutableSet<String> {
        val currentNodes = mutableSetOf(startNode)
        val visited = mutableSetOf<String>()
        while(currentNodes.isNotEmpty()) {
            val current = currentNodes.first()
            currentNodes.remove(current)
            visited.add(current)

            val next = nodes[current]!!
            next.filter { !visited.contains(it) }.forEach { currentNodes.add(it) }
        }
        return visited
    }

    fun part1(input: List<String>): Int {
        // Parse input
        val nodes = mutableMapOf<String, MutableSet<String>>()
        input.forEach { line ->
            val parts = line.split(':', ' ').filter { it.isNotBlank() }
            val node = parts[0]
            val otherNodes = parts.drop(1)
            otherNodes.forEach { n->
                addEdge(node, n, nodes)
            }
        }

        // Verify walkGraph() works for full graph
        checkEquals(nodes.size, walkGraph(nodes.keys.first(), nodes).size)

        // Cuts to make according to visual inspection in graphviz:
        //    bdj/vfh
        //    ztc/ttv
        //    bnv/rpd
        breakEdge("bdj", "vfh", nodes)
        breakEdge("ztc", "ttv", nodes)
        breakEdge("bnv", "rpd", nodes)

        val subgraph1 = walkGraph("bdj", nodes)
        val subgraph2 = walkGraph("vfh", nodes)

        check(subgraph1.size < nodes.size)
        check(subgraph2.size < nodes.size)

        return subgraph1.size * subgraph2.size
    }

//    fun part2(input: List<String>): Int {
//        return input.size
//    }

//    val testInput = readInput("Day25_test")
//    check(part1(testInput) == 1)

    val input = readInput("Day25")
    println(part1(input))
    //println(part2(input))
}
