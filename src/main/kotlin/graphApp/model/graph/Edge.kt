package graphApp.model.graph

import kotlinx.serialization.Serializable

@Serializable
open class Edge(
    open val from: Vertex,
    open val to: Vertex,
    open val weight: Double = 1.0
) {
    open fun isDirected(): Boolean = true
}
