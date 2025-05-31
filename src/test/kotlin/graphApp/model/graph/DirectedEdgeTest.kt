import graphApp.model.graph.DirectedEdge
import graphApp.model.graph.Vertex
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import kotlin.test.Test

class DirectedEdgeTest {
    private val v1 = Vertex("A")
    private val v2 = Vertex("B")

    @Test
    fun `edge should be directed`() {
        val edge = DirectedEdge(v1, v2)
        assertTrue(edge.isDirected())
    }

    @Test
    fun `toString shows directed arrow`() {
        val edge = DirectedEdge(v1, v2)
        assertEquals("A → B", edge.toString())
    }
}
