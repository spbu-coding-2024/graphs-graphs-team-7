import graphApp.model.graph.Vertex
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class VertexTest {
    @Test
    fun `vertex equality by id`() {
        val v1 = Vertex("A")
        val v2 = Vertex("A")
        assertEquals(v1, v2)
    }

    @Test
    fun `toString returns id`() {
        val v = Vertex("TEST")
        assertEquals("TEST", v.toString())
    }
}