
import graphApp.model.algorithms.shortestpath.Dijkstra
import graphApp.model.graph.Edge
import graphApp.model.graph.Graph
import graphApp.model.graph.Vertex
import graphApp.model.graph.WeightedEdge
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

private fun <T: Edge> Graph<T>.addVertices(vararg vertices: Vertex) {
    vertices.forEach { addVertex(it) }
}

private fun <T: Edge> Graph<T>.addEdges(vararg edges: T) {
    edges.forEach { addEdge(it) }
}

internal class DijkstraTest {

    @Test
    fun `empty graph throws exception`() {
        val graph = Graph<Edge>()
        val a = Vertex("A")
        val b = Vertex("B")

        assertThrows<IllegalArgumentException> {
            Dijkstra.findShortestPath(graph, a, b)
        }.also { e ->
            assertThat(e.message, containsString("Start vertex not in graph"))
        }
    }

    @Test
    fun `same start and end returns zero distance`() {
        val a = Vertex("A")
        val graph = Graph<Edge>().apply { addVertex(a) }

        val result = Dijkstra.findShortestPath(graph, a, a)!!

        assertThat(result.path, contains(a))
        assertThat(result.distance, `is`(0.0))
    }

    @Test
    fun `single edge path`() {
        val a = Vertex("A")
        val b = Vertex("B")
        val graph = Graph<Edge>().apply {
            addVertices(a, b)
            addEdge(WeightedEdge(a, b, 5.0))
        }

        val result = Dijkstra.findShortestPath(graph, a, b)!!

        assertThat(result.path, contains(a, b))
        assertThat(result.distance, `is`(5.0))
    }

    @Test
    fun `multiple paths selects shortest`() {
        val (a, b, c) = createVertices(3)
        val graph = Graph<Edge>().apply {
            addVertices(a, b, c)
            addEdges(
                WeightedEdge(a, b, 1.0),
                WeightedEdge(a, c, 4.0),
                WeightedEdge(b, c, 2.0)
            )
        }

        val result = Dijkstra.findShortestPath(graph, a, c)!!

        assertThat(result.path, contains(a, b, c))
        assertThat(result.distance, `is`(3.0))
    }

    @Test
    fun `unreachable vertex returns null`() {
        val a = Vertex("A")
        val b = Vertex("B")
        val c = Vertex("C")
        val graph = Graph<Edge>().apply {
            addVertices(a, b, c)
            addEdge(WeightedEdge(a, b, 1.0))
        }

        val result = Dijkstra.findShortestPath(graph, a, c)
        assertThat(result, nullValue())
    }

    @Test
    fun `negative edge weight throws exception`() {
        val a = Vertex("A")
        val b = Vertex("B")
        val graph = Graph<Edge>().apply {
            addVertices(a, b)
            addEdge(WeightedEdge(a, b, -1.0))
        }

        assertThrows<IllegalArgumentException> {
            Dijkstra.findShortestPath(graph, a, b)
        }.also { e ->
            assertThat(e.message, containsString("contains negative weight edges"))
        }
    }

    @Test
    fun `non-weighted edge throws exception`() {
        val a = Vertex("A")
        val b = Vertex("B")
        val graph = Graph<Edge>().apply {
            addVertices(a, b)
            addEdge(Edge(a, b))
        }

        assertThrows<IllegalArgumentException> {
            Dijkstra.findShortestPath(graph, a, b)
        }.also { e ->
            assertThat(e.message, containsString("All edges must be weighted"))
        }
    }

    @Test
    fun `handles zero weight edges`() {
        val (a, b, c) = createVertices(3)
        val graph = Graph<Edge>().apply {
            addVertices(a, b, c)
            addEdges(
                WeightedEdge(a, b, 0.0),
                WeightedEdge(b, c, 0.0)
            )
        }

        val result = Dijkstra.findShortestPath(graph, a, c)!!

        assertThat(result.path, contains(a, b, c))
        assertThat(result.distance, `is`(0.0))
    }

    @Test
    fun `multiple edges between vertices`() {
        val a = Vertex("A")
        val b = Vertex("B")
        val graph = Graph<Edge>().apply {
            addVertices(a, b)
            addEdges(
                WeightedEdge(a, b, 5.0),
                WeightedEdge(a, b, 3.0)
            )
        }

        val result = Dijkstra.findShortestPath(graph, a, b)!!

        assertThat(result.path, contains(a, b))
        assertThat(result.distance, `is`(3.0))
    }

    private fun createVertices(count: Int): List<Vertex> =
        (1..count).map { Vertex("V$it") }
}