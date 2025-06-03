package graphApp.model.graph

open class DirectedGraph : Graph<DirectedEdge>() {

    fun addEdge(from: Vertex, to: Vertex) {
        addEdge(DirectedEdge(from, to))
    }

    override fun getNeighbors(vertex: Vertex): List<Vertex> {
        return edges
            .filter { it.from == vertex }
            .map { it.to }
    }

    fun transpose(): DirectedGraph {
        val transposed = DirectedGraph()
        getAllVertices().forEach { transposed.addVertex(it) }
        getAllEdges().forEach {
            transposed.addEdge(DirectedEdge(it.to, it.from))
        }
        return transposed
    }

    fun getOutDegree(vertex: Vertex): Int =
        edges.count { it.from == vertex }

    fun getInDegree(vertex: Vertex): Int =
        edges.count { it.to == vertex }
}
