package graphApp.model.graph

import graphApp.model.graph.serialization.SerializableEdge
import graphApp.model.graph.serialization.SerializableGraph

open class Graph<T : Edge> {
    val vertices = mutableSetOf<Vertex>()
    val edges = mutableListOf<T>()
    val positions = mutableMapOf<Vertex, Position>()

    open fun addVertex(vertex: Vertex) {
        require(vertex !in vertices) { "Vertex $vertex already exists in the graph" }
        vertices.add(vertex)
        positions[vertex] = Position(0f, 0f)
    }

    open fun addEdge(edge: T) {
        require(edge.from in vertices) { "Source vertex ${edge.from} not found" }
        require(edge.to in vertices) { "Target vertex ${edge.to} not found" }
        edges.add(edge)
    }

    fun removeVertex(vertex: Vertex) {
        vertices.remove(vertex)
        edges.removeAll { it.from == vertex || it.to == vertex }
        positions.remove(vertex)
    }

    fun removeEdge(edge: T) = edges.remove(edge)

    open fun getNeighbors(vertex: Vertex): List<Vertex> =
        edges.filter { it.from == vertex }.map { it.to }

    fun containsVertex(vertex: Vertex): Boolean =
        getAllVertices().contains(vertex)
    fun containsEdge(from: Vertex, to: Vertex): Boolean =
        edges.any { it.from == from && it.to == to }

    fun setPosition(vertex: Vertex, x: Float, y: Float) {
        require(vertex in vertices) { "Vertex $vertex not in graph" }
        positions[vertex] = Position(x, y)
    }

    open fun getEdgesFrom(vertex: Vertex): List<T> {
        return edges.filter { it.from == vertex }
    }

    open fun getEdgesTo(vertex: Vertex): List<T> {
        return edges.filter { it.to == vertex }
    }

    open fun getEdgeWeight(from: Vertex, to: Vertex): Double? {
        return null
    }

    fun getPosition(vertex: Vertex): Position? = positions[vertex]

    fun copy(): Graph<T> {
        val newGraph = Graph<T>()

        val vertexMap = mutableMapOf<Vertex, Vertex>()
        this.vertices.forEach { oldVertex ->
            val newVertex = Vertex(oldVertex.id).apply {
            }
            vertexMap[oldVertex] = newVertex
            newGraph.addVertex(newVertex)
        }

        this.edges.forEach { oldEdge ->
            val newFrom = vertexMap[oldEdge.from] ?: error("Vertex not found in map")
            val newTo = vertexMap[oldEdge.to] ?: error("Vertex not found in map")
            @Suppress("UNCHECKED_CAST") val newEdge = when (oldEdge) {
                is DirectedWeightedEdge -> DirectedWeightedEdge(newFrom, newTo, oldEdge.weight)
                is WeightedEdge -> WeightedEdge(newFrom, newTo, oldEdge.weight)
                is DirectedEdge -> DirectedEdge(newFrom, newTo)
                else -> Edge(newFrom, newTo)
            } as T
            newGraph.addEdge(newEdge)
        }

        this.positions.forEach { (oldVertex, pos) ->
            val newVertex = vertexMap[oldVertex] ?: error("Vertex not found in map")
            newGraph.setPosition(newVertex, pos.x, pos.y)
        }

        return newGraph
    }

    fun getAllVertices(): Set<Vertex> = vertices.toSet()
    fun getAllEdges(): List<T> = edges.toList()
    fun isEmpty(): Boolean = vertices.isEmpty()


    fun toSerializable(): SerializableGraph {
        return SerializableGraph(
            vertices = vertices.toList(),
            edges = edges.map { it.toSerializableEdge() },
            positions = positions.mapKeys { it.key.id }
        )
    }

    companion object {
        fun fromSerializable(
            serializable: SerializableGraph
        ): Graph<Edge> {
            val graph = Graph<Edge>()

            val vertexMap = serializable.vertices.associateBy { it.id }

            vertexMap.values.forEach { graph.addVertex(it) }

            serializable.edges.forEach { edge ->
                val from = vertexMap[edge.from] ?: error("Vertex ${edge.from} not found")
                val to = vertexMap[edge.to] ?: error("Vertex ${edge.to} not found")

                val newEdge = when (edge) {
                    is SerializableEdge.Directed -> DirectedEdge(from, to)
                    is SerializableEdge.Weighted -> WeightedEdge(from, to, edge.weight)
                    is SerializableEdge.DirectedWeighted -> DirectedWeightedEdge(from, to, edge.weight)
                }

                graph.addEdge(newEdge)
            }

            serializable.positions.forEach { (id, pos) ->
                vertexMap[id]?.let { graph.setPosition(it, pos.x, pos.y) }
            }

            return graph
        }
    }
}

fun Edge.toSerializableEdge(): SerializableEdge = when (this) {
    is DirectedEdge -> SerializableEdge.Directed(from.id, to.id)
    is WeightedEdge -> SerializableEdge.Weighted(from.id, to.id, weight)
    else -> throw IllegalArgumentException("Unsupported edge type")
}