package graphApp.model.graph

import kotlinx.serialization.Serializable

@Serializable
data class Position(
    val x: Float,
    val y: Float,
) {
    fun distanceTo(other: Position): Float {
        val dx = x - other.x
        val dy = y - other.y
        return kotlin.math.sqrt(dx * dx + dy * dy)
    }
    fun add(dx: Float, dy: Float): Position {
        return Position(x + dx, y + dy)
    }
}