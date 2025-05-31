package graphApp.model.algorithms.layout

import graphApp.model.graph.Edge
import graphApp.model.graph.Graph
import graphApp.model.graph.Vertex
import graphApp.model.graph.WeightedEdge
import kotlin.math.ln
import kotlin.math.sqrt

class ForceAtlas2 {
    // Настройки алгоритма
    var scalingRatio = 2.0
    var gravity = 1.0
    var linLogMode = false
    var preventOverlap = false
    var edgeWeightInfluence = 1.0
    var tolerance = 0.1
    var maxIterations = 100

    fun <T : Edge> applyLayout(graph: Graph<T>) {
        initializePositions(graph)
        repeat(maxIterations) { iteration ->
            val forces = mutableMapOf<Vertex, Pair<Double, Double>>().withDefault { Pair(0.0, 0.0) }
            calculateRepulsionForces(graph, forces)
            calculateAttractionForces(graph, forces)
            applyForces(graph, forces, iteration)
        }
    }

    private fun <T : Edge> initializePositions(graph: Graph<T>) {
        graph.vertices.forEach { vertex ->
            if (graph.getPosition(vertex) == null) {
                graph.setPosition(vertex, (Math.random() * 100).toFloat(), (Math.random() * 100).toFloat())
            }
        }
    }

    private fun <T : Edge> calculateRepulsionForces(
        graph: Graph<T>,
        forces: MutableMap<Vertex, Pair<Double, Double>>
    ) {
        graph.vertices.forEach { v1 ->
            graph.vertices.forEach { v2 ->
                if (v1 != v2) {
                    val pos1 = graph.getPosition(v1)!!
                    val pos2 = graph.getPosition(v2)!!
                    val dx = pos1.x.toDouble() - pos2.x.toDouble()
                    val dy = pos1.y.toDouble() - pos2.y.toDouble()
                    val distance = sqrt(dx * dx + dy * dy)
                    if (distance > 0) {
                        val repulsion = scalingRatio * scalingRatio / distance
                        forces[v1] = forces.getValue(v1).let { (fx, fy) ->
                            Pair(fx + dx / distance * repulsion, fy + dy / distance * repulsion)
                        }
                    }
                }
            }
        }
    }

    private fun <T : Edge> calculateAttractionForces(
        graph: Graph<T>,
        forces: MutableMap<Vertex, Pair<Double, Double>>
    ) {
        graph.edges.forEach { edge ->
            val posFrom = graph.getPosition(edge.from)!!
            val posTo = graph.getPosition(edge.to)!!
            val dx = posFrom.x.toDouble() - posTo.x.toDouble()
            val dy = posFrom.y.toDouble() - posTo.y.toDouble()
            val distance = sqrt(dx * dx + dy * dy)
            if (distance > 0) {
                val weight = (edge as? WeightedEdge)?.weight ?: 1.0
                val attraction = if (linLogMode) {
                    ln(1 + distance) / distance
                } else {
                    distance / (scalingRatio * (1 + edgeWeightInfluence * weight))
                }
                forces[edge.from] = forces.getValue(edge.from).let { (fx, fy) ->
                    Pair(fx - dx * attraction, fy - dy * attraction)
                }
                forces[edge.to] = forces.getValue(edge.to).let { (fx, fy) ->
                    Pair(fx + dx * attraction, fy + dy * attraction)
                }
            }
        }
    }

    private fun <T : Edge> applyForces(
        graph: Graph<T>,
        forces: Map<Vertex, Pair<Double, Double>>,
        iteration: Int
    ) {
        val temperature = (1 - iteration.toDouble() / maxIterations) * scalingRatio
        graph.positions.forEach { (vertex, pos) ->
            val (fx, fy) = forces[vertex] ?: Pair(0.0, 0.0)
            val newX = pos.x + (fx * temperature).toFloat()
            val newY = pos.y + (fy * temperature).toFloat()
            graph.setPosition(vertex, newX, newY)
        }
    }
}