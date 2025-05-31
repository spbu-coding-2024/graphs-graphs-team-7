package graphApp.model.graph.serialization

import graphApp.model.graph.Position
import graphApp.model.graph.Vertex
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SerializableGraph(
    val vertices: List<Vertex>,
    val edges: List<SerializableEdge>,
    val positions: Map<String, Position>
)

@Serializable
sealed class SerializableEdge {
    abstract val from: String
    abstract val to: String

    @Serializable
    @SerialName("directed")
    data class Directed(
        override val from: String,
        override val to: String
    ) : SerializableEdge()

    @Serializable
    @SerialName("weighted")
    data class Weighted(
        override val from: String,
        override val to: String,
        val weight: Double
    ) : SerializableEdge()

    @Serializable
    @SerialName("directed_weighted")
    data class DirectedWeighted(
        override val from: String,
        override val to: String,
        val weight: Double
    ) : SerializableEdge()
}
