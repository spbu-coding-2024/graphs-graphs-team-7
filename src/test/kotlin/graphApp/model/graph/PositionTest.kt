import graphApp.model.graph.Position
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PositionTest {
    @Test
    fun `distance calculation correct`() {
        val p1 = Position(0f, 0f)
        val p2 = Position(3f, 4f)
        assertEquals(5f, p1.distanceTo(p2))
    }
}