package graphApp.model.algorithms.layout

import graphApp.model.graph.Graph
import graphApp.model.graph.Position
import graphApp.model.graph.Vertex
import kotlin.math.hypot
import kotlin.math.max

class ForceAtlas2 {

    var iterations = 10
    private val epsilon = 0.01f
    private val damping = 0.8f
    var scalingRatio = 20f

    fun applyLayout(graph: Graph<*>) {
        val vertices = graph.getAllVertices().toList()
        if (vertices.isEmpty()) return

        val velocities = mutableMapOf<Vertex, Position>()
        vertices.forEach { vertex ->
            velocities[vertex] = Position(0f, 0f)
        }

        repeat(iterations) {
            applyRepulsion(vertices, graph.positions, velocities)

            graph.getAllEdges().forEach { edge ->
                applyAttraction(edge.from, edge.to, graph.positions, velocities)
            }

            updatePositions(vertices, graph.positions, velocities)
        }
    }

    private fun applyRepulsion(
        vertices: List<Vertex>,
        positions: Map<Vertex, Position>,
        velocities: MutableMap<Vertex, Position>
    ) {
        for (i in vertices.indices) {
            val v1 = vertices[i]
            val pos1 = positions[v1] ?: continue

            for (j in i + 1 until vertices.size) {
                val v2 = vertices[j]
                val pos2 = positions[v2] ?: continue

                val dx = pos1.x - pos2.x
                val dy = pos1.y - pos2.y
                val distance = max(hypot(dx, dy), epsilon)

                val force = (scalingRatio * scalingRatio) / distance

                velocities[v1] = velocities[v1]!!.add(dx / distance * force, dy / distance * force)
                velocities[v2] = velocities[v2]!!.add(-dx / distance * force, -dy / distance * force)
            }
        }
    }

    private fun applyAttraction(
        from: Vertex,
        to: Vertex,
        positions: Map<Vertex, Position>,
        velocities: MutableMap<Vertex, Position>
    ) {
        val posFrom = positions[from] ?: return
        val posTo = positions[to] ?: return

        val dx = posFrom.x - posTo.x
        val dy = posFrom.y - posTo.y
        val distance = max(hypot(dx, dy), epsilon)

        val force = distance / scalingRatio

        velocities[from] = velocities[from]!!.add(-dx / distance * force, -dy / distance * force)
        velocities[to] = velocities[to]!!.add(dx / distance * force, dy / distance * force)
    }

    private fun updatePositions(
        vertices: List<Vertex>,
        positions: MutableMap<Vertex, Position>,
        velocities: MutableMap<Vertex, Position>
    ) {
        vertices.forEach { vertex ->
            val velocity = velocities[vertex]!!
            val position = positions[vertex]!!

            positions[vertex] = Position(
                position.x + velocity.x,
                position.y + velocity.y
            )

            velocities[vertex] = Position(
                velocity.x * damping,
                velocity.y * damping
            )
        }
    }
}