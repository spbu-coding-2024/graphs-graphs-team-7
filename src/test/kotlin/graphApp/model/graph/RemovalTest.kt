import graphApp.model.graph.*
import org.junit.Assert.*
import kotlin.test.Test

class RemovalTests {
    private val v1 = Vertex("A")
    private val v2 = Vertex("B")

    @Test
    fun `remove vertex should clean edges`() {
        val graph = DirectedGraph().apply {
            addVertex(v1)
            addVertex(v2)
            addEdge(v1, v2)
        }

        graph.removeVertex(v1)

        assertFalse(graph.containsVertex(v1))
        assertEquals(0, graph.edges.size)
    }

    @Test
    fun `remove non-existent edge should return false`() {
        val graph = Graph<Edge>()
        assertFalse(graph.removeEdge(Edge(v1, v2)))
    }

    @Test
    fun `graph invariants after multiple operations`() {
        val graph = DirectedWeightedGraph()
        val vertices = List(10) { Vertex("V$it") }

        vertices.forEach(graph::addVertex)

        repeat(100) {
            val from = vertices.random()
            val to = vertices.random()
            if (from != to) {
                graph.addEdge(from, to, (1..10).random().toDouble())
            }
        }

        assertTrue(graph.edges.all { it.from in vertices })
        assertTrue(graph.edges.all { it.to in vertices })
        assertEquals(
            graph.edges.size,
            vertices.sumOf { graph.outDegree(it) }
        )
    }
}
