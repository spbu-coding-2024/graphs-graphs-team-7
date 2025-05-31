
import graphApp.model.graph.DirectedWeightedGraph
import graphApp.model.graph.Vertex
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

class DirectedWeightedGraphTest {
    private lateinit var graph: DirectedWeightedGraph
    private val v1 = Vertex("A")
    private val v2 = Vertex("B")
    private val v3 = Vertex("C")

    @Before
    fun setup() {
        graph = DirectedWeightedGraph()
        graph.addVertex(v1)
        graph.addVertex(v2)
        graph.addVertex(v3)
    }

    @Test
    fun `add edge between two vertices`() {
        graph.addEdge(v1, v2, 5.0)
        assertEquals(1, graph.edges.size)
        assertEquals(5.0, graph.getEdgeWeight(v1, v2))
    }

    @Test
    fun `prevent self-loop edge`() {
        assertFailsWith<IllegalArgumentException> {
            graph.addEdge(v1, v1, 2.0)
        }
    }

    @Test
    fun `update existing edge weight`() {
        graph.addEdge(v1, v2, 3.0)
        graph.updateEdgeWeight(v1, v2, 10.0)
        assertEquals(10.0, graph.getEdgeWeight(v1, v2))
    }

    @Test
    fun `calculate in-degree and out-degree`() {
        graph.addEdge(v1, v2, 1.0)
        graph.addEdge(v3, v2, 2.0)
        graph.addEdge(v2, v3, 3.0)

        assertEquals(2, graph.inDegree(v2))
        assertEquals(1, graph.outDegree(v2))
    }

    @Test
    fun `prevent adding edge with negative weight`() {
        assertFailsWith<IllegalArgumentException> {
            graph.addEdge(v1, v2, -1.0)
        }
    }

    @Test
    fun `hasNegativeWeights returns false for valid graph`() {
        graph.addEdge(v1, v2, 5.0)
        graph.addEdge(v2, v3, 3.0)
        assertFalse(graph.hasNegativeWeights())
    }
}