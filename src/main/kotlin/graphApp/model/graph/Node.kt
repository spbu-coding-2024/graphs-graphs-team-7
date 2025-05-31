package graphApp.model.graph

data class Node(
    val id: String,
    val label: String = "",
    val x: Float = 0f,
    val y: Float = 0f
)