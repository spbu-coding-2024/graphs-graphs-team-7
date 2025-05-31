package graphApp.model.graph

import kotlinx.serialization.Serializable

@Serializable
data class Vertex(val id: String) {
    override fun toString(): String = id.replace("V", "")
}