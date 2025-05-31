package graphApp.model.graph

class DirectedWeightedGraph : Graph<DirectedWeightedEdge>() {

    fun addEdge(from: Vertex, to: Vertex, weight: Double) {
        require(from != to) { "Cannot create edge from vertex to itself" }
        addEdge(DirectedWeightedEdge(from, to, weight))
    }

    private fun getIncomingEdges(vertex: Vertex): List<DirectedWeightedEdge> =
        edges.filter { it.to == vertex }

    private fun getOutgoingEdges(vertex: Vertex): List<DirectedWeightedEdge> =
        edges.filter { it.from == vertex }

    override fun getEdgeWeight(from: Vertex, to: Vertex): Double? =
        edges.firstOrNull { it.from == from && it.to == to }?.weight

    fun inDegree(vertex: Vertex): Int = getIncomingEdges(vertex).size
    fun outDegree(vertex: Vertex): Int = getOutgoingEdges(vertex).size
    fun totalWeight(): Double = edges.sumOf { it.weight }

    fun hasNegativeWeights(): Boolean = edges.any { it.weight < 0 }
}
