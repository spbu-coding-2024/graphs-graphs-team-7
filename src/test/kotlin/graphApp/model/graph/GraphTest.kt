import graphApp.model.graph.Edge
import graphApp.model.graph.Graph
import graphApp.model.graph.Vertex
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GraphTest {

    private lateinit var graph: Graph<Edge>

    @BeforeEach
    fun setUp() {
        graph = Graph()
    }

    @Test
    fun `addVertex should add a vertex to the graph`() {
        val vertex = Vertex(1.toString())
        graph.addVertex(vertex)

        assertTrue(graph.containsVertex(vertex))
        assertEquals(1, graph.getAllVertices().size)
    }

    @Test
    fun `addVertex should throw exception if vertex already exists`() {
        val vertex = Vertex(1.toString())
        graph.addVertex(vertex)

        assertThrows(IllegalArgumentException::class.java) {
            graph.addVertex(vertex)
        }
    }

    @Test
    fun `addEdge should add an edge between two vertices`() {
        val vertex1 = Vertex(1.toString())
        val vertex2 = Vertex(2.toString())
        graph.addVertex(vertex1)
        graph.addVertex(vertex2)

        val edge = Edge(vertex1, vertex2)
        graph.addEdge(edge)

        assertTrue(graph.containsEdge(vertex1, vertex2))
        assertEquals(1, graph.getAllEdges().size)
    }

    @Test
    fun `addEdge should throw exception if source or target vertex is not in the graph`() {
        val vertex1 = Vertex(1.toString())
        val vertex2 = Vertex(2.toString())

        val edge = Edge(vertex1, vertex2)

        assertThrows(IllegalArgumentException::class.java) {
            graph.addEdge(edge)
        }
    }

    @Test
    fun `removeVertex should remove a vertex and its associated edges`() {
        val vertex1 = Vertex(1.toString())
        val vertex2 = Vertex(2.toString())
        graph.addVertex(vertex1)
        graph.addVertex(vertex2)

        val edge = Edge(vertex1, vertex2)
        graph.addEdge(edge)

        graph.removeVertex(vertex1)

        assertFalse(graph.containsVertex(vertex1))
        assertFalse(graph.containsEdge(vertex1, vertex2))
        assertEquals(0, graph.getAllEdges().size)
    }

    @Test
    fun `getNeighbors should return neighbors of a vertex`() {
        val vertex1 = Vertex(1.toString())
        val vertex2 = Vertex(2.toString())
        val vertex3 = Vertex(3.toString())
        graph.addVertex(vertex1)
        graph.addVertex(vertex2)
        graph.addVertex(vertex3)

        graph.addEdge(Edge(vertex1, vertex2))
        graph.addEdge(Edge(vertex1, vertex3))

        val neighbors = graph.getNeighbors(vertex1)

        assertEquals(2, neighbors.size)
        assertTrue(neighbors.contains(vertex2))
        assertTrue(neighbors.contains(vertex3))
    }

    @Test
    fun `setPosition should set position for a vertex`() {
        val vertex = Vertex(1.toString())
        graph.addVertex(vertex)

        graph.setPosition(vertex, 10f, 20f)

        val position = graph.getPosition(vertex)
        assertNotNull(position)
        assertEquals(10f, position?.x)
        assertEquals(20f, position?.y)
    }

    @Test
    fun `setPosition should throw exception if vertex is not in the graph`() {
        val vertex = Vertex(1.toString())

        assertThrows(IllegalArgumentException::class.java) {
            graph.setPosition(vertex, 10f, 20f)
        }
    }

    @Test
    fun `copy should create a deep copy of the graph`() {
        val vertex1 = Vertex(1.toString())
        val vertex2 = Vertex(2.toString())
        graph.addVertex(vertex1)
        graph.addVertex(vertex2)
        graph.addEdge(Edge(vertex1, vertex2))
        graph.setPosition(vertex1, 10f, 20f)

        val copiedGraph = graph.copy()

        assertEquals(graph.getAllVertices(), copiedGraph.getAllVertices())
        assertEquals(graph.getAllEdges().size, copiedGraph.getAllEdges().size)
        assertEquals(graph.getPosition(vertex1), copiedGraph.getPosition(copiedGraph.getAllVertices().first { it.id == 1.toString() }))
    }

    @Test
    fun `toSerializable and fromSerializable should serialize and deserialize graph correctly`() {
        val vertex1 = Vertex(1.toString())
        val vertex2 = Vertex(2.toString())
        graph.addVertex(vertex1)
        graph.addVertex(vertex2)
        graph.addEdge(Edge(vertex1, vertex2))
        graph.setPosition(vertex1, 10f, 20f)

        val serializable = graph.toSerializable()
        val deserializedGraph = Graph.fromSerializable(serializable)

        assertEquals(graph.getAllVertices().size, deserializedGraph.getAllVertices().size)
        assertEquals(graph.getAllEdges().size, deserializedGraph.getAllEdges().size)
        assertEquals(graph.getPosition(vertex1), deserializedGraph.getPosition(deserializedGraph.getAllVertices().first { it.id == 1.toString() }))
    }
}
