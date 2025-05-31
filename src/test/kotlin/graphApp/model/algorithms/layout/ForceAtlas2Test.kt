package graphApp.model.algorithms.layout

import graphApp.model.graph.Edge
import graphApp.model.graph.Graph
import graphApp.model.graph.Vertex
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test
import kotlin.math.hypot
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ForceAtlas2Test {

    private fun createGraph(): Graph<Edge> = Graph()

    @Test
    fun `should handle empty graph`() {
        val graph = createGraph()
        val layout = ForceAtlas2()
        assertDoesNotThrow { layout.applyLayout(graph) }
    }

    @Test
    fun `should handle single vertex`() {
        val graph = createGraph().apply {
            addVertex(Vertex("A"))
        }
        val layout = ForceAtlas2()
        layout.applyLayout(graph)

        val position = graph.positions[graph.getAllVertices().first()]
        assertNotNull(position)
        assertEquals(0f, position.x, 0.01f)
        assertEquals(0f, position.y, 0.01f)
    }

    @Test
    fun `should position two connected vertices`() {
        val graph = createGraph().apply {
            val a = Vertex("A")
            val b = Vertex("B")
            addVertex(a)
            addVertex(b)
            addEdge(Edge(a, b))
        }
        val layout = ForceAtlas2()
        layout.applyLayout(graph)

        val positions = graph.positions.values.toList()
        val distance = hypot(
            (positions[0].x - positions[1].x).toDouble(),
            (positions[0].y - positions[1].y).toDouble()
        )
        assertTrue(distance in 10.0..100.0, "Distance: $distance")
    }

    @Test
    fun `should create reasonable layout for complete graph`() {
        val n = 5
        val graph = createCompleteGraph(n)
        val layout = ForceAtlas2()
        layout.applyLayout(graph)

        val positions = graph.positions.values.toList()
        assertEquals(n, positions.size)

        val centerX = positions.sumOf { it.x.toDouble() } / n
        val centerY = positions.sumOf { it.y.toDouble() } / n
        assertEquals(0.0, centerX, 1.0, "Center X: $centerX")
        assertEquals(0.0, centerY, 1.0, "Center Y: $centerY")
    }

    private fun createCompleteGraph(n : Int): Graph<Edge> {
        val graph = createGraph()
        val vertices = List(n) { Vertex("V$it") }

        vertices.forEach { graph.addVertex(it) }

        for (i in 0 until 5) {
            for (j in i + 1 until 5) {
                graph.addEdge(Edge(vertices[i], vertices[j]))
            }
        }
        return graph
    }
}