package graphApp.model.algorithms.shortestpath

import graphApp.model.graph.Edge
import graphApp.model.graph.Graph
import graphApp.model.graph.Vertex
import graphApp.model.graph.WeightedEdge
import java.util.*
import kotlin.Double.Companion.POSITIVE_INFINITY

object Dijkstra {
    data class ShortestPathResult(
        val path: List<Vertex>,
        val distance: Double
    )

    fun findShortestPath(
        graph: Graph<out Edge>,
        start: Vertex,
        end: Vertex
    ): ShortestPathResult? {
        val weightedEdges = graph.getAllEdges().filterIsInstance<WeightedEdge>()
        if (weightedEdges.size != graph.getAllEdges().size) {
            throw IllegalArgumentException("All edges must be weighted for Dijkstra algorithm")
        }

        require(graph.containsVertex(start)) { "Start vertex not in graph" }
        require(graph.containsVertex(end)) { "End vertex not in graph" }

        if (weightedEdges.any { it.weight < 0 }) {
            throw IllegalArgumentException("Graph contains negative weight edges")
        }

        val distances = mutableMapOf<Vertex, Double>().withDefault { POSITIVE_INFINITY }
        val previous = mutableMapOf<Vertex, Vertex?>()
        val queue = PriorityQueue<Pair<Vertex, Double>>(compareBy { it.second })

        distances[start] = 0.0
        queue.add(start to 0.0)

        while (queue.isNotEmpty()) {
            val (current, currentDist) = queue.poll()

            if (currentDist > distances.getValue(current)) continue

            if (current == end) break

            graph.getEdgesFrom(current).forEach { edge ->
                val weightedEdge = edge as? WeightedEdge
                    ?: throw IllegalStateException("Encountered non-weighted edge in Dijkstra algorithm")

                val neighbor = weightedEdge.to
                val newDistance = distances.getValue(current) + weightedEdge.weight

                if (newDistance < distances.getValue(neighbor)) {
                    distances[neighbor] = newDistance
                    previous[neighbor] = current
                    queue.add(neighbor to newDistance)
                }
            }
        }

        return if (distances[end]!! < POSITIVE_INFINITY) {
            ShortestPathResult(
                path = reconstructPath(previous, end),
                distance = distances[end]!!
            )
        } else {
            null
        }
    }

    private fun reconstructPath(
        previous: Map<Vertex, Vertex?>,
        end: Vertex
    ): List<Vertex> {
        val path = mutableListOf<Vertex>()
        var current: Vertex? = end

        while (current != null) {
            path.add(current)
            current = previous[current]
        }

        return path.asReversed()
    }
}
