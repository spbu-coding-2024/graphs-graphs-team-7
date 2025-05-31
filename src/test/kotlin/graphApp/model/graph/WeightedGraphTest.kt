import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import graphApp.model.graph.WeightedGraph
import graphApp.model.graph.Vertex

class WeightedGraphTest {
    private lateinit var graph: WeightedGraph
    private val v1 = Vertex("A")
    private val v2 = Vertex("B")

    @BeforeEach
    fun setup() {
        graph = WeightedGraph()
        graph.addVertex(v1)
        graph.addVertex(v2)
    }

    @Test
    fun `update non-existent edge weight should do nothing`() {
        graph.updateEdgeWeight(v1, v2, 5.0)
        assertNull(graph.getEdgeWeight(v1, v2))
    }

    @Test
    fun `add multiple weighted edges`() {
        graph.addEdge(v1, v2, 1.5)
        graph.addEdge(v2, v1, 2.5)

        assertEquals(2, graph.edges.size)
        assertEquals(1.5, graph.getEdgeWeight(v1, v2))
    }

    @Test
    fun `edges should have correct weights after update`() {
        graph.addEdge(v1, v2, 3.0)
        graph.updateEdgeWeight(v1, v2, 5.0)

        val edge = graph.getEdgesFrom(v1).first()
        assertEquals(5.0, edge.weight)
    }
}