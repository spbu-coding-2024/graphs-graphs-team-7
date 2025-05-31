import graphApp.model.graph.DirectedEdge
import graphApp.model.graph.DirectedGraph
import graphApp.model.graph.Vertex
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DirectedGraphTest {

    private lateinit var graph: DirectedGraph
    private lateinit var directedGraph: DirectedGraph

    private val v1 = Vertex("V1")
    private val v2 = Vertex("V2")
    private val v3 = Vertex("V3")
    private val v4 = Vertex("V4")

    @BeforeEach
    fun setup() {
        graph = DirectedGraph()
        directedGraph = DirectedGraph()
        graph.addVertex(v1)
        graph.addVertex(v2)
        graph.addVertex(v3)
        graph.addVertex(v4)
    }

    @Test
    fun `should add directed edge`() {
        graph.addEdge(v1, v2)
        graph.addEdge(DirectedEdge(v2, v3))

        assertEquals(2, graph.edges.size)
        assertEquals(v2, graph.edges[0].to)
        assertEquals(v3, graph.edges[1].to)
    }

    @Test
    fun `addEdge should add a directed edge between two vertices`() {
        val vertex1 = Vertex(1.toString())
        val vertex2 = Vertex(2.toString())
        directedGraph.addVertex(vertex1)
        directedGraph.addVertex(vertex2)

        directedGraph.addEdge(vertex1, vertex2)

        assertTrue(directedGraph.containsEdge(vertex1, vertex2))
        assertFalse(directedGraph.containsEdge(vertex2, vertex1))
        assertEquals(1, directedGraph.getAllEdges().size)
    }

    @Test
    fun `getNeighbors should return neighbors for a vertex in a directed graph`() {
        val vertex1 = Vertex(1.toString())
        val vertex2 = Vertex(2.toString())
        val vertex3 = Vertex(3.toString())
        directedGraph.addVertex(vertex1)
        directedGraph.addVertex(vertex2)
        directedGraph.addVertex(vertex3)

        directedGraph.addEdge(vertex1, vertex2)
        directedGraph.addEdge(vertex1, vertex3)

        val neighbors = directedGraph.getNeighbors(vertex1)

        assertEquals(2, neighbors.size)
    }

    @Test
    fun `transpose should reverse all edges in the graph`() {
        val vertex1 = Vertex(1.toString())
        val vertex2 = Vertex(2.toString())
        directedGraph.addVertex(vertex1)
        directedGraph.addVertex(vertex2)

        directedGraph.addEdge(vertex1, vertex2)

        val transposedGraph = directedGraph.transpose()

        assertTrue(transposedGraph.containsEdge(vertex2, vertex1))
        assertFalse(transposedGraph.containsEdge(vertex1, vertex2))
        assertEquals(1, transposedGraph.getAllEdges().size)
    }

    @Test
    fun `getOutDegree should return the number of outgoing edges from a vertex`() {
        val vertex1 = Vertex(1.toString())
        val vertex2 = Vertex(2.toString())
        val vertex3 = Vertex(3.toString())
        directedGraph.addVertex(vertex1)
        directedGraph.addVertex(vertex2)
        directedGraph.addVertex(vertex3)

        directedGraph.addEdge(vertex1, vertex2)
        directedGraph.addEdge(vertex1, vertex3)

        assertEquals(2, directedGraph.getOutDegree(vertex1))
        assertEquals(0, directedGraph.getOutDegree(vertex2))
    }

    @Test
    fun `getInDegree should return the number of incoming edges to a vertex`() {
        val vertex1 = Vertex(1.toString())
        val vertex2 = Vertex(2.toString())
        val vertex3 = Vertex(3.toString())
        directedGraph.addVertex(vertex1)
        directedGraph.addVertex(vertex2)
        directedGraph.addVertex(vertex3)

        directedGraph.addEdge(vertex1, vertex2)
        directedGraph.addEdge(vertex3, vertex2)

        assertEquals(0, directedGraph.getInDegree(vertex1))
        assertEquals(2, directedGraph.getInDegree(vertex2))
        assertEquals(0, directedGraph.getInDegree(vertex3))
    }

    @Test
    fun `adding an edge should update both in-degree and out-degree`() {
        val vertex1 = Vertex(1.toString())
        val vertex2 = Vertex(2.toString())
        directedGraph.addVertex(vertex1)
        directedGraph.addVertex(vertex2)

        directedGraph.addEdge(vertex1, vertex2)

        assertEquals(1, directedGraph.getOutDegree(vertex1))
        assertEquals(0, directedGraph.getOutDegree(vertex2))
        assertEquals(0, directedGraph.getInDegree(vertex1))
        assertEquals(1, directedGraph.getInDegree(vertex2))
    }

    @Test
    fun `getNeighbors should return outgoing vertices`() {
        graph.addEdge(v1, v2)
        graph.addEdge(v1, v3)
        graph.addEdge(v2, v3)

        val neighbors = graph.getNeighbors(v1)
        assertEquals(2, neighbors.size)
        assertTrue(neighbors.containsAll(listOf(v2, v3)))
        assertFalse(neighbors.contains(v4))
    }

    @Test
    fun `transpose should reverse all edges`() {
        // Original: V1 -> V2, V2 -> V3
        graph.addEdge(v1, v2)
        graph.addEdge(v2, v3)

        val transposed = graph.transpose()

        assertEquals(2, transposed.edges.size)
        assertEquals(v1, transposed.edges.find { it.from == v2 }?.to)
        assertEquals(v2, transposed.edges.find { it.from == v3 }?.to)
    }

    @Test
    fun `transpose should preserve all vertices`() {
        graph.addEdge(v1, v2)
        val transposed = graph.transpose()

        assertEquals(4, transposed.vertices.size)
        assertTrue(transposed.vertices.containsAll(listOf(v1, v2, v3, v4)))
    }

    @Test
    fun `getOutDegree should count outgoing edges`() {
        graph.addEdge(v1, v2)
        graph.addEdge(v1, v3)
        graph.addEdge(v2, v3)

        assertEquals(2, graph.getOutDegree(v1))
        assertEquals(1, graph.getOutDegree(v2))
        assertEquals(0, graph.getOutDegree(v3))
    }

    @Test
    fun `getInDegree should count incoming edges`() {
        graph.addEdge(v1, v2)
        graph.addEdge(v1, v3)
        graph.addEdge(v2, v3)
        graph.addEdge(v3, v4)

        assertEquals(0, graph.getInDegree(v1))
        assertEquals(1, graph.getInDegree(v2))
        assertEquals(2, graph.getInDegree(v3))
        assertEquals(1, graph.getInDegree(v4))
    }

    @Test
    fun `should not allow adding duplicate edges`() {
        graph.addEdge(v1, v2)
        graph.addEdge(v1, v2)

        assertEquals(1, graph.edges.size)
    }

    @Test
    fun `should handle isolated vertices`() {
        assertEquals(0, graph.getOutDegree(v4))
        assertEquals(0, graph.getInDegree(v4))
        assertTrue(graph.getNeighbors(v4).isEmpty())
    }

    @Test
    fun `transpose of empty graph should be empty`() {
        val emptyGraph = DirectedGraph()
        val transposed = emptyGraph.transpose()

        assertTrue(transposed.vertices.isEmpty())
        assertTrue(transposed.edges.isEmpty())
    }

    @Test
    fun `should maintain vertex positions after transpose`() {
        graph.setPosition(v1, 10f, 20f)
        graph.setPosition(v2, 30f, 40f)

        val transposed = graph.transpose()

        assertEquals(10f, transposed.getPosition(v1)?.x)
        assertEquals(20f, transposed.getPosition(v1)?.y)
        assertEquals(30f, transposed.getPosition(v2)?.x)
        assertEquals(40f, transposed.getPosition(v2)?.y)
    }
}
