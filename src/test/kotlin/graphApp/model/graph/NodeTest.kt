import graphApp.model.graph.Node
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class NodeTest {
    @Test
    fun `node creation with coordinates`() {
        val node = Node("1", "Node1", 100f, 200f)

        assertEquals(100f, node.x)
        assertEquals("Node1", node.label)
        assertEquals("1", node.id)
    }

    @Test
    fun `node equality by id`() {
        val node1 = Node("1", "Test", 0f, 0f)
        val node2 = Node("1", "Different", 10f, 20f)
        assertEquals(node1, node2)
    }
}
