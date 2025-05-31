package graphApp.model.graph

open class WeightedGraph : Graph<WeightedEdge>() {

    fun addEdge(from: Vertex, to: Vertex, weight: Double) {
        require(from in getAllVertices()) { "Vertex $from not in graph" }
        require(to in getAllVertices()) { "Vertex $to not in graph" }
        addEdge(WeightedEdge(from, to, weight))
    }

    fun updateEdgeWeight(from: Vertex, to: Vertex, newWeight: Double) {
        val edge = getAllEdges().find { it.from == from && it.to == to }
        edge?.let {
            removeEdge(it)
            addEdge(WeightedEdge(from, to, newWeight))
        }
    }
}