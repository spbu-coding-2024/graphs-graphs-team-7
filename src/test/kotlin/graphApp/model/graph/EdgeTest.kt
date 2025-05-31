import graphApp.model.graph.Edge
import graphApp.model.graph.Vertex
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class EdgeTest {

    private lateinit var vertex1: Vertex
    private lateinit var vertex2: Vertex

    @BeforeEach
    fun setUp() {
        vertex1 = Vertex(1.toString())
        vertex2 = Vertex(2.toString())
    }

    @Test
    fun `Edge should be created with default weight`() {
        val edge = Edge(vertex1, vertex2)

        assertEquals(vertex1, edge.from)
        assertEquals(vertex2, edge.to)
        assertEquals(1.0, edge.weight)
    }

    @Test
    fun `Edge should be created with custom weight`() {
        val edge = Edge(vertex1, vertex2, 5.0)

        assertEquals(vertex1, edge.from)
        assertEquals(vertex2, edge.to)
        assertEquals(5.0, edge.weight)
    }

    @Test
    fun `isDirected should return true by default`() {
        val edge = Edge(vertex1, vertex2)

        assertTrue(edge.isDirected())
    }

    @Test
    fun `Edges with the same vertices and weight should be equal`() {
        val edge1 = Edge(vertex1, vertex2, 3.0)
        val edge2 = Edge(vertex1, vertex2, 3.0)

        assertEquals(edge1, edge2)
        assertEquals(edge1.hashCode(), edge2.hashCode())
    }

    @Test
    fun `Edges with different vertices or weight should not be equal`() {
        val edge1 = Edge(vertex1, vertex2, 3.0)
        val edge2 = Edge(vertex2, vertex1, 3.0)
        val edge3 = Edge(vertex1, vertex2, 5.0)

        assertNotEquals(edge1, edge2)
        assertNotEquals(edge1, edge3)
    }

    @Test
    fun `Edge should be serializable`() {
        val edge = Edge(vertex1, vertex2, 3.0)

        val json = Json.encodeToString(edge)
        val deserializedEdge = Json.decodeFromString<Edge>(json)

        assertEquals(edge.from, deserializedEdge.from)
        assertEquals(edge.to, deserializedEdge.to)
        assertEquals(edge.weight, deserializedEdge.weight)
    }
}
