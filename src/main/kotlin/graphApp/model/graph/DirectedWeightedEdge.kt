package graphApp.model.graph

class DirectedWeightedEdge(
    from: Vertex,
    to: Vertex,
    weight: Double = 1.0
) : WeightedEdge(from, to, weight) {
    override fun toString() = "${from.id}→${to.id} ($weight)"
}