import graphApp.model.graph.DirectedWeightedEdge
import graphApp.model.graph.Vertex
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DirectedWeightedEdgeTest {
    private val v1 = Vertex("A")
    private val v2 = Vertex("B")

    @Test
    fun `negative weight should throw`() {
        assertThrows(IllegalArgumentException::class.java) {
            DirectedWeightedEdge(v1, v2, -1.0)
        }
    }

    @Test
    fun `toString shows weight`() {
        val edge = DirectedWeightedEdge(v1, v2, 3.14)
        assertTrue(edge.toString().contains("3.14"))
    }
}
