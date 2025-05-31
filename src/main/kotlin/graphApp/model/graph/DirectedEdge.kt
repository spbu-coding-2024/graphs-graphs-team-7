package graphApp.model.graph

class DirectedEdge(
    override val from: Vertex,
    override val to: Vertex
) : Edge(from, to) {
    override fun isDirected(): Boolean = true

    override fun toString(): String = "$from → $to"
}
