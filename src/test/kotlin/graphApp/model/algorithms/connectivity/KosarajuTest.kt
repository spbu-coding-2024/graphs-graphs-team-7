
import graphApp.model.algorithms.connectivity.Kosaraju
import graphApp.model.graph.DirectedEdge
import graphApp.model.graph.DirectedGraph
import graphApp.model.graph.Graph
import graphApp.model.graph.Vertex
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class KosarajuTest {
    private val v1 = Vertex("A")
    private val v2 = Vertex("B")
    private val v3 = Vertex("C")
    private val v4 = Vertex("D")
    private val v5 = Vertex("E")

    @Test
    fun `empty graph returns empty list`() {
        val graph = Graph<DirectedEdge>()
        val components = Kosaraju.findStronglyConnectedComponents(graph)
        assertThat(components, empty())
    }

    @Test
    fun `single vertex forms one component`() {
        val a = Vertex("A")
        val graph = Graph<DirectedEdge>().apply { addVertex(a) }

        val components = Kosaraju.findStronglyConnectedComponents(graph)

        assertThat(components, contains(containsInAnyOrder(a)))
    }

    @Test
    fun `two strongly connected vertices form one component`() {
        val a = Vertex("A")
        val b = Vertex("B")
        val graph = Graph<DirectedEdge>().apply {
            addVertex(a)
            addVertex(b)
            addEdge(DirectedEdge(a, b))
            addEdge(DirectedEdge(b, a))
        }

        val components = Kosaraju.findStronglyConnectedComponents(graph)

        assertThat(components, contains(containsInAnyOrder(a, b)))
    }

    @Test
    fun `two weakly connected vertices form two components`() {
        val a = Vertex("A")
        val b = Vertex("B")
        val graph = Graph<DirectedEdge>().apply {
            addVertex(a)
            addVertex(b)
            addEdge(DirectedEdge(a, b))
        }

        val components = Kosaraju.findStronglyConnectedComponents(graph)

        assertThat(components, containsInAnyOrder(
            containsInAnyOrder(a),
            containsInAnyOrder(b)
        ))
    }

    @Test
    fun `single strongly connected component`() {
        val graph = DirectedGraph().apply {
            addEdge(v1, v2)
            addEdge(v2, v3)
            addEdge(v3, v1)
        }

        val components = Kosaraju.findStronglyConnectedComponents(graph)
        assertEquals(1, components.size)
        assertTrue(components[0].containsAll(listOf(v1, v2, v3)))
    }

    @Test
    fun `multiple disconnected components`() {
        val graph = DirectedGraph().apply {
            addEdge(v1, v2)
            addEdge(v2, v1)
            addEdge(v3, v4)
            addEdge(v4, v3)
        }

        val components = Kosaraju.findStronglyConnectedComponents(graph)
        assertEquals(2, components.size)
        assertTrue(components.any { it.toSet() == setOf(v1, v2) })
        assertTrue(components.any { it.toSet() == setOf(v3, v4) })
    }

    @Test
    fun `empty graph should return empty list`() {
        val graph = DirectedGraph()
        assertTrue(Kosaraju.findStronglyConnectedComponents(graph).isEmpty())
    }

    @Test
    fun `single vertex graph`() {
        val graph = DirectedGraph().apply { addVertex(v1) }
        val components = Kosaraju.findStronglyConnectedComponents(graph)
        assertEquals(1, components.size)
        assertEquals(listOf(v1), components[0])
    }

    @Test
    fun `nested strongly connected components`() {
        val graph = DirectedGraph().apply {
            addEdge(v1, v2)
            addEdge(v2, v3)
            addEdge(v3, v1)

            val v4 = Vertex("D")
            val v5 = Vertex("E")
            addEdge(v4, v5)
            addEdge(v5, v4)

            addEdge(v1, v4)
        }

        val components = Kosaraju.findStronglyConnectedComponents(graph)
            .sortedByDescending { it.size }

        assertEquals(2, components.size)
        assertEquals(setOf(v1, v2, v3), components[0].toSet())
        assertEquals(setOf(v4, v5), components[1].toSet())
    }

    @Test
    fun `vertex with no outgoing edges`() {
        val graph = DirectedGraph().apply {
            addEdge(v1, v2)
            addEdge(v2, v3)
        }

        val components = Kosaraju.findStronglyConnectedComponents(graph)
            .sortedBy { it.size }

        assertEquals(3, components.size)
        assertTrue(components.any { it == listOf(v1) })
        assertTrue(components.any { it == listOf(v2) })
        assertTrue(components.any { it == listOf(v3) })
    }
}