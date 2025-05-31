package graphApp.model.graph

import androidx.compose.ui.geometry.Offset

data class GraphState(
    val graph: Graph<Edge>?,
    val vertexCounter: Int,
    val scale: Float,
    val offset: Offset
)