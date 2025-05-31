import graphApp.model.algorithms.connectivity.Kosaraju
import graphApp.model.algorithms.layout.ForceAtlas2
import graphApp.model.algorithms.shortestpath.Dijkstra
import graphApp.model.graph.DirectedGraph
import graphApp.model.graph.Vertex
import graphApp.model.graph.WeightedEdge
import graphApp.model.graph.WeightedGraph
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@Tag("integration")
class LayoutAndConnectivityIntegrationTest {

    @Test
    fun `force atlas layout should not break kosaraju components`() {
        val graph = DirectedGraph().apply {
            val v1 = Vertex("A")
            val v2 = Vertex("B")
            val v3 = Vertex("C")
            val v4 = Vertex("D")

            addEdge(v1, v2)
            addEdge(v2, v3)
            addEdge(v3, v1)
            addEdge(v4, v4)
        }

        val initialComponents = Kosaraju.findStronglyConnectedComponents(graph)

        ForceAtlas2().apply {
            iterations = 50
            scalingRatio = 3.0f
        }.applyLayout(graph)

        val componentsAfterLayout = Kosaraju.findStronglyConnectedComponents(graph)

        assertEquals(
            initialComponents.sortedBy { it.size },
            componentsAfterLayout.sortedBy { it.size },
            "Layout algorithm changed graph structure"
        )

        val selfLoopComponent = componentsAfterLayout.first { it.size == 1 }
        assertEquals(1, selfLoopComponent.size)
        assertEquals("D", selfLoopComponent.first().id)
    }

    @Test
    @Tag("integration")
    fun `layout should not affect shortest path calculation`() {
        val graph = WeightedGraph().apply {
            val v1 = Vertex("A")
            val v2 = Vertex("B")
            val v3 = Vertex("C")

            addEdge(WeightedEdge(v1, v2, 1.0))
            addEdge(WeightedEdge(v2, v3, 2.0))
            addEdge(WeightedEdge(v1, v3, 5.0))
        }

        val initialPath = Dijkstra.findShortestPath(graph, Vertex("A"), Vertex("C"))!!

        ForceAtlas2().applyLayout(graph)

        val pathAfterLayout = Dijkstra.findShortestPath(graph, Vertex("A"), Vertex("C"))!!

        assertEquals(initialPath.distance, pathAfterLayout.distance)
        assertEquals(initialPath.path, pathAfterLayout.path)
    }
}