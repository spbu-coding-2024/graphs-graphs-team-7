
import graphApp.model.graph.Vertex
import graphApp.viewmodel.AlgorithmResult
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

class AlgorithmResultTest {

    private val v1 = Vertex("V1")
    private val v2 = Vertex("V2")
    private val v3 = Vertex("V3")

    @Test
    fun `ShortestPath should store correct data`() {
        val path = listOf(v1, v2, v3)
        val distance = 5.0
        val result = AlgorithmResult.ShortestPath(path, distance)

        assertAll(
            { assertEquals(path, result.path) },
            { assertEquals(distance, result.distance) },
        )
    }

    @Test
    fun `ConnectedComponents should store correct data`() {
        val components = listOf(listOf(v1, v2), listOf(v3))
        val result = AlgorithmResult.ConnectedComponents(components)

        assertAll(
            { assertEquals(components, result.components) },
            { assertEquals(2, result.components.size) },
        )
    }

    @Test
    fun `MessageResult should store correct data`() {
        val message = "Test message"
        val result = AlgorithmResult.MessageResult(message)

        assertAll(
            { assertEquals(message, result.message) },
        )
    }

    @Test
    fun `ShortestPath equals should work correctly`() {
        val path1 = listOf(v1, v2)
        val path2 = listOf(v1, v2, v3)

        val result1 = AlgorithmResult.ShortestPath(path1, 2.0)
        val result2 = AlgorithmResult.ShortestPath(path1, 2.0)
        val result3 = AlgorithmResult.ShortestPath(path2, 2.0)
        val result4 = AlgorithmResult.ShortestPath(path1, 3.0)

        assertEquals(result1, result2)
        assertNotEquals(result1, result3)
        assertNotEquals(result1, result4)
        assertNotEquals(result1, "string")
        assertNotEquals(result1, null)
    }

    @Test
    fun `ConnectedComponents equals should work correctly`() {
        val comp1 = listOf(listOf(v1))
        val comp2 = listOf(listOf(v1, v2))

        val result1 = AlgorithmResult.ConnectedComponents(comp1)
        val result2 = AlgorithmResult.ConnectedComponents(comp1)
        val result3 = AlgorithmResult.ConnectedComponents(comp2)

        assertEquals(result1, result2)
        assertNotEquals(result1, result3)
    }

    @Test
    fun `MessageResult equals should work correctly`() {
        val result1 = AlgorithmResult.MessageResult("Error")
        val result2 = AlgorithmResult.MessageResult("Error")
        val result3 = AlgorithmResult.MessageResult("Warning")

        assertEquals(result1, result2)
        assertNotEquals(result1, result3)
    }

    @Test
    fun `hashCode consistency check`() {
        val pathResult = AlgorithmResult.ShortestPath(listOf(v1, v2), 3.0)
        val compResult = AlgorithmResult.ConnectedComponents(listOf(listOf(v1)))
        val msgResult = AlgorithmResult.MessageResult("Test")

        assertAll(
            { assertEquals(pathResult.hashCode(), pathResult.hashCode()) },
            { assertEquals(compResult.hashCode(), compResult.hashCode()) },
            { assertEquals(msgResult.hashCode(), msgResult.hashCode()) }
        )
    }

    @Test
    fun `when expressions should work with sealed class`() {
        fun processResult(result: AlgorithmResult): String = when (result) {
            is AlgorithmResult.ShortestPath -> "Path: ${result.path.size} vertices"
            is AlgorithmResult.ConnectedComponents -> "Components: ${result.components.size}"
            is AlgorithmResult.MessageResult -> "Message: ${result.message}"
        }

        val pathResult = AlgorithmResult.ShortestPath(listOf(v1, v2), 5.0)
        val compResult = AlgorithmResult.ConnectedComponents(listOf(listOf(v1, v2)))
        val msgResult = AlgorithmResult.MessageResult("Done")

        assertAll(
            { assertEquals("Path: 2 vertices", processResult(pathResult)) },
            { assertEquals("Components: 1", processResult(compResult)) },
            { assertEquals("Message: Done", processResult(msgResult)) }
        )
    }
}