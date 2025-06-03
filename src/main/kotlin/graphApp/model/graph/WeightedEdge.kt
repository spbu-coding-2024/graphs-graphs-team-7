package graphApp.model.graph

open class WeightedEdge(from: Vertex, to: Vertex, override var weight: Double) : Edge(from, to) {
    override fun toString() = "${super.toString()} ($weight)"
}