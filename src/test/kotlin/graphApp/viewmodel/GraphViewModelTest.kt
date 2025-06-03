import graphApp.model.algorithms.AlgorithmType
import graphApp.model.graph.Vertex
import graphApp.model.graph.WeightedEdge
import graphApp.view.components.dialogs.GraphType
import graphApp.viewmodel.GraphViewModel
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GraphViewModelTest {

    private lateinit var viewModel: GraphViewModel

    @Test
    fun `initial state should be empty`() {
        assertNull(viewModel.graph.value)
        assertFalse(viewModel.canUndo)
        assertFalse(viewModel.canRedo)
    }

    @Test
    fun `addEdge should connect vertices`() {
        val v1 = Vertex("V1")
        val v2 = Vertex("V2")

        viewModel.addEdge(v1, v2)

        val graph = viewModel.graph.value
        assertNotNull(graph)
        assertEquals(1, graph?.edges?.size)
        assertTrue(graph?.edges?.first() is WeightedEdge)
    }

    @Test
    fun `generateTree should create tree structure`() {
        viewModel.generateGraph(GraphType.TREE, 5, 0.5, 1.0, 5.0)

        val graph = viewModel.graph.value
        assertNotNull(graph)
        assertEquals(5, graph?.vertices?.size)
        assertTrue((graph?.edges?.size ?: 0) >= 4)
    }

    @Test
    fun `importFromJson should restore graph state`() {
        val json = """
        {
            "vertices": [{"id":"V1"}, {"id":"V2"}],
            "edges": [
                {"type":"weighted","from":"V1","to":"V2","weight":3.0}
            ],
            "positions": {
                "V1": {"x":10.0, "y":20.0},
                "V2": {"x":30.0, "y":40.0}
            }
        }
        """.trimIndent()

        viewModel.importFromJson(json)

        val graph = viewModel.graph.value
        assertNotNull(graph)
        assertEquals(2, graph?.vertices?.size)
        assertEquals(1, graph?.edges?.size)

        val positions = graph?.positions
        assertEquals(10f, positions?.values?.elementAt(0)?.x)
        assertEquals(40f, positions?.values?.elementAt(1)?.y)
    }

    @Test
    fun `findVertexAt should detect vertex in radius`() {
        viewModel.addVertexAtPosition(100f, 100f)
        val vertex = viewModel.graph.value!!.vertices.first()

        val found = viewModel.findVertexAt(110f, 110f)
        assertEquals(vertex, found)

        val notFound = viewModel.findVertexAt(140f, 140f)
        assertNull(notFound)
    }

    @Test
    fun `handlePan should update offset`() {
        val initialOffset = viewModel.offset
        viewModel.handlePan(50f, -30f)

        assertEquals(initialOffset.x + 50f, viewModel.offset.x)
        assertEquals(initialOffset.y - 30f, viewModel.offset.y)
    }

    @Test
    fun `handleZoom should update scale within bounds`() {
        viewModel.handleZoom(0.5f)
        assertEquals(1.5f, viewModel.scale)

        viewModel.handleZoom(-0.9f)
        assertEquals(0.15f, viewModel.scale, 0.01f)

        viewModel.handleZoom(-1f)
        assertEquals(0.01f, viewModel.scale, 0.001f)

        viewModel.scale = 2.5f
        viewModel.handleZoom(1f)
        assertEquals(3f, viewModel.scale)
    }

    @Test
    fun `clearGraph should reset all state`() {
        viewModel.addVertexAtPosition(0f, 0f)
        viewModel.addVertexAtPosition(100f, 100f)
        viewModel.selectedStart = viewModel.graph.value?.vertices?.first()

        viewModel.clearGraph()

        assertTrue(viewModel.graph.value?.vertices.isNullOrEmpty())
        assertNull(viewModel.selectedStart)
        assertNull(viewModel.selectedEnd)
        assertNull(viewModel.algorithmResult.value)
    }

    @Test
    fun `forceLayout should reposition vertices`() {
        viewModel.addVertexAtPosition(0f, 0f)
        viewModel.addVertexAtPosition(10f, 10f)

        val initialPositions = viewModel.graph.value!!.positions.values.toList()

        viewModel.runAlgorithm(AlgorithmType.FORCEATLAS2)

        val newPositions = viewModel.graph.value!!.positions.values.toList()
        assertNotEquals(initialPositions, newPositions)
    }
}
